/*
public class MainActivity  {

    private final String PREFERENCE_NOTEPAD = "notepad";


    private final String BUNDLE_VISIBLE_NOTEPAD = "visiblenotepad";


    protected void onCreate(Bundle savedInstanceState) {

        goFirstChangeView(savedInstanceState);
        try {
            db = Database2020.getToWriting(this);
            setDayOfSubject();
            refreshSubjectsCursors();
            setAdapter();
            refreshActionBarInfo();
        } catch (SQLiteException e) {
           // Toast.makeText(this, R.string.error_database, Toast.LENGTH_SHORT).show();
        }

        loadNotepad();

       // if(NotificationOnline.getWifiConnecting(this)) {
       //     NotificationOnline checkInformationOnline = new NotificationOnline(this);
            checkInformationOnline.execute();
        }
        NotificationsChannels.CreateNotificationsChannels(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        try {
            refreshSubjectsCursors();
            refreshActionBarInfo();
        } catch (SQLiteException e){
            Toast.makeText(this, R.string.error_database, Toast.LENGTH_SHORT).show();
        }
        invalidateOpt*/
/**//*
ionsMenu();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(isNotepadEmpty()){
            DrawableCompat.setTint(menu.findItem(R.id.action_notepad).getIcon(), ContextCompat.getColor(this, android.R.color.darker_gray));
        } else {
            DrawableCompat.setTint(menu.findItem(R.id.action_notepad).getIcon(), ContextCompat.getColor(this, android.R.color.holo_red_light));
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void goFirstChangeView(final Bundle bundle){
        ViewTreeObserver viewTreeObserver = recyclerView.getViewTreeObserver();

        if (viewTreeObserver.isAlive()){
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                    View bottomButtons = findViewById(R.id.main_bottom_buttons);
//                    recyclerView.setPadding(recyclerView.getPaddingLeft(), recyclerView.getPaddingTop(), recyclerView.getPaddingRight(), bottomButtons.getHeight());

                    if(bundle == null || !bundle.getBoolean(BUNDLE_VISIBLE_NOTEPAD)) showNotepad();
                }
            });
        }
    }

    private void showNotepad() {
        if (notepadBox.getTranslationY() != 0) {
            notepadBox.animate()
                    .translationY(0);
            recyclerView.setPadding(recyclerView.getPaddingLeft(), notepadBox.getHeight() + getResources().getDimensionPixelSize(R.dimen.margin_card) * 2, recyclerView.getPaddingRight(), recyclerView.getPaddingBottom());
        } else {
            notepadBox.animate()
                    .translationY(notepadBox.getHeight() * -1 - getResources().getDimensionPixelSize(R.dimen.margin_card) * 2);
            recyclerView.setPadding(recyclerView.getPaddingLeft(), 0, recyclerView.getPaddingRight(), recyclerView.getPaddingBottom());
        }
        invalidateOptionsMenu();
    }


    private void saveNotepad(){
        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        editor.putString(PREFERENCE_NOTEPAD, editTextNotepad.getText().toString().trim());
        editor.apply();
    }

    private void loadNotepad(){
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        editTextNotepad.setText(sharedPreferences.getString(PREFERENCE_NOTEPAD, ""));
    }

    private boolean isNotepadEmpty(){
        if(editTextNotepad.getText().toString().trim().equals("")){
            return true;
        } else {
            return false;
        }
    }*/
