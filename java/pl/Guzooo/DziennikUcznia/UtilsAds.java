package pl.Guzooo.DziennikUcznia;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.MuteThisAdReason;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;

import java.util.ArrayList;
import java.util.List;

public class UtilsAds {

    private static ArrayList<Ad> ads = new ArrayList<>();

    public static void initialization(Context context){
        MobileAds.initialize(context);
    }

    public static void showAd(String adId, ViewGroup adPlace, Context context){
        showAd(adId, false, adPlace, context);
    }

    public static void showAd(String adId, boolean mediaEnable, ViewGroup adPlace, Context context){
        new AdLoader.Builder(context, adId)
                .forUnifiedNativeAd(getUnifiedNativeAd(mediaEnable, adPlace, context))
                .withNativeAdOptions(getNativeAdOption())
                .build()
                .loadAd(new AdRequest.Builder().build());
    }

    private static UnifiedNativeAd.OnUnifiedNativeAdLoadedListener getUnifiedNativeAd(final boolean mediaEnable, final ViewGroup adPlace, final Context context){
        return new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener(){
            @Override
            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                      UnifiedNativeAdView adView = (UnifiedNativeAdView) LayoutInflater.from(context).inflate(R.layout.ad, null);
                      populateUnifiedNativeAdView(unifiedNativeAd, adView, mediaEnable);
                      adPlace.removeAllViews();
                      adPlace.addView(adView);
                      addToList(unifiedNativeAd, context);
            }
        };
    }

    private static NativeAdOptions getNativeAdOption(){
        return new NativeAdOptions.Builder()
                .setRequestCustomMuteThisAd(true)
                .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_BOTTOM_LEFT)
                .build();
    }

    public static void destroyAds(Context context){
        for(Ad ad : ads){
            if(ad.getContext() == context){
                for(UnifiedNativeAd openAd : ad.getAds()){
                    openAd.destroy();
                }
                ads.remove(ad);
            }
        }
    }

    private static class Ad {
        private Context context;
        private ArrayList<UnifiedNativeAd> ads = new ArrayList<>();

        Ad(Context context){
            this.context = context;
        }

        private void addAd(UnifiedNativeAd ad){
            ads.add(ad);
        }

        private Context getContext(){
            return context;
        }

        private ArrayList<UnifiedNativeAd> getAds(){
            return ads;
        }
    }

    private static void populateUnifiedNativeAdView(UnifiedNativeAd ad, UnifiedNativeAdView adView, boolean mediaEnable){
        setIcon(ad, adView);
        setTitle(ad, adView);
        setDescription(ad, adView);
        setCallToAction(ad, adView);
        setAdvertiser(ad, adView);
        setShopInfo(ad, adView);
        if(mediaEnable)
            setMedia(adView);

        setCustomMuteAd(ad, adView);

        adView.setNativeAd(ad);
    }

    private static void addToList(UnifiedNativeAd newAd, Context context){
        for(Ad ad : ads) {
            if (ad.getContext() == context) {
                ad.addAd(newAd);
                return;
            }
        }
        Ad ad = new Ad(context);
        ad.addAd(newAd);
        ads.add(ad);
    }

    private static void setIcon(UnifiedNativeAd ad, UnifiedNativeAdView adView){
        ImageView iconView = adView.findViewById(R.id.image);
        NativeAd.Image icon = ad.getIcon();
        if(icon == null)
            icon = ad.getImages().get(0);
        iconView.setImageDrawable(icon.getDrawable());
        adView.setIconView(iconView);
    }

    private static void setTitle(UnifiedNativeAd ad, UnifiedNativeAdView adView){
        TextView titleView = adView.findViewById(R.id.title);
        String title = ad.getHeadline();
        titleView.setText(title);
        adView.setHeadlineView(titleView);
    }

    private static void setDescription(UnifiedNativeAd ad, UnifiedNativeAdView adView){
        TextView descriptionView = adView.findViewById(R.id.description);
        String description = ad.getBody();
        descriptionView.setText(description);
        adView.setBodyView(descriptionView);
    }

    private static void setCallToAction(UnifiedNativeAd ad, UnifiedNativeAdView adView){
        TextView callToActionView = adView.findViewById(R.id.button);
        String callToAction = ad.getCallToAction();
        callToActionView.setText(callToAction);
        adView.setCallToActionView(callToActionView);
    }

    private static void setMedia(UnifiedNativeAdView adView) {
        MediaView mediaView = adView.findViewById(R.id.media);
        adView.setVisibility(View.VISIBLE);
        adView.setMediaView(mediaView);
    }

    private static void setAdvertiser(UnifiedNativeAd ad, UnifiedNativeAdView adView){
        TextView advertiserView = adView.findViewById(R.id.advertiser);
        String advertiser = ad.getAdvertiser();
        if(advertiser != null){
            advertiserView.setText(ad.getAdvertiser());
            adView.setAdvertiserView(advertiserView);
        } else {
            advertiserView.setVisibility(View.GONE);
            adView.findViewById(R.id.advertiser_separator).setVisibility(View.GONE);
        }
    }

    private static void setShopInfo(UnifiedNativeAd ad, UnifiedNativeAdView adView){
        setStars(ad, adView);
        setShop(ad, adView);
        setPrice(ad, adView);
    }

    private static void setCustomMuteAd(UnifiedNativeAd ad, UnifiedNativeAdView adView){
        if(ad.isCustomMuteThisAdEnabled()) {
            showCustomMute(ad.getMuteThisAdReasons(), ad, adView);
            closeCustomMute(adView);
        } else
            hideCustomMute(adView);
    }

    private static void setStars(UnifiedNativeAd ad, UnifiedNativeAdView adView){
        TextView starsView = adView.findViewById(R.id.stars);
        Double stars = ad.getStarRating();
        if (stars != null) {
            starsView.setText(adView.getContext().getString(R.string.rating, stars));
            adView.setStarRatingView(starsView);
        } else {
            starsView.setVisibility(View.GONE);
            adView.findViewById(R.id.stars_separator).setVisibility(View.GONE);
        }
    }

    private static void setShop(UnifiedNativeAd ad, UnifiedNativeAdView adView){
        TextView shopView = adView.findViewById(R.id.shop);
        String shop = ad.getStore();
        if (shop != null) {
            shopView.setText(shop);
            adView.setStoreView(shopView);
        } else {
            shopView.setVisibility(View.GONE);
        }
    }

    private static void setPrice (UnifiedNativeAd ad, UnifiedNativeAdView adView){
        TextView priceView = adView.findViewById(R.id.price);
        String price = ad.getPrice();
        if (price != null) {
            priceView.setText(price);
            adView.setPriceView(priceView);
        } else {
            priceView.setVisibility(View.GONE);
            adView.findViewById(R.id.price_separator).setVisibility(View.GONE);
        }
    }

    private static void showCustomMute(List<MuteThisAdReason> reasons, UnifiedNativeAd ad, UnifiedNativeAdView adView){
        View button = adView.findViewById(R.id.close);
        ViewGroup closeList = adView.findViewById(R.id.close_list);
        View closeView = adView.findViewById(R.id.close_view);
        View.OnClickListener openListener = getOnClickMuteListener(closeView);
        button.setOnClickListener(openListener);
        for(MuteThisAdReason reason : reasons){
            TextView textView;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
                textView = new TextView(closeList.getContext(),null,0, R.style.TextClickable);
             else
                textView = new Button(closeList.getContext());

            textView.setText(reason.getDescription());
            View.OnClickListener clickReasonListener = getOnClickReasonListener(ad, adView, reason);
            textView.setOnClickListener(clickReasonListener);
            closeList.addView(textView);
        }
    }

    private static View.OnClickListener getOnClickMuteListener(final View closeView){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilsAnimation.showCircle(closeView, UtilsAnimation.BOTTOM);
            }
        };
    }

    private static View.OnClickListener getOnClickReasonListener(final UnifiedNativeAd ad, final UnifiedNativeAdView adView, final MuteThisAdReason reason){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.muteThisAd(reason);
                adView.removeAllViews();
                adView.setVisibility(View.GONE);
            }
        };
    }

    private static void closeCustomMute(UnifiedNativeAdView adView){
        View button = adView.findViewById(R.id.close_back);
        View closeView = adView.findViewById(R.id.close_view);
        View.OnClickListener closeListener = getOnClickCloseMuteListener(closeView);
        button.setOnClickListener(closeListener);
    }

    private static View.OnClickListener getOnClickCloseMuteListener(final View closeView){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilsAnimation.hideCircle(closeView, UtilsAnimation.BOTTOM);
            }
        };
    }

    private static void hideCustomMute(UnifiedNativeAdView adView){
        adView.findViewById(R.id.close).setVisibility(View.GONE);
    }
}
