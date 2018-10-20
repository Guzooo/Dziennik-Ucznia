package pl.Guzooo.DziennikUcznia;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

public class LessonPlanActivity extends Activity {

    private RecyclerView recyclerView;

    private Cursor cursor;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_plan);

        recyclerView = findViewById(R.id.plan_recycler);

        try {
            db = StaticMethod.getReadableDatabase(this);
            refreshCursor();
            setAdapter();
        }catch (SQLiteException e){
            Toast.makeText(this, R.string.error_database, Toast.LENGTH_SHORT).show();
        }
        goFirstChangeView();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        try {
            refreshCursor();
            setAdapter();
        }catch (SQLiteException e){
            Toast.makeText(this, R.string.error_database, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        cursor.close();
        db.close();
    }

    public void ClickAdd(View v){
        goToLessonPlanEditActivity(0);
    }

    private void goFirstChangeView(){
        ViewTreeObserver viewTreeObserver = recyclerView.getViewTreeObserver();

        if (viewTreeObserver.isAlive()){
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    View bottomButtons = findViewById(R.id.plan_bottom_button);
                    recyclerView.setPadding(recyclerView.getPaddingLeft(), recyclerView.getPaddingTop(), recyclerView.getPaddingRight(), bottomButtons.getHeight());
                }
            });
        }
    }

    private void refreshCursor(){
        cursor = db.query("LESSON_PLAN",
                SubjectPlan.subjectPlanOnCursor,
                null, null, null, null,
                "DAY, TIME_START");
    }

    private void setAdapter(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        AdapterPlanCardView adapter = new AdapterPlanCardView(cursor, findViewById(R.id.plan_plan_null));
        recyclerView.setAdapter(adapter);

        adapter.setListener(new AdapterPlanCardView.Listener() {
            @Override
            public void onClick(int id) {
                goToLessonPlanEditActivity(id);
            }
        });
    }

    private void goToLessonPlanEditActivity(int id){
        Intent intent = new Intent(getApplicationContext(), LessonPlanEditActivity.class);
        intent.putExtra(LessonPlanEditActivity.EXTRA_ID, id);
        startActivity(intent);
    }
}
