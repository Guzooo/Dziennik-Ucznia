package pl.Guzooo.DziennikUcznia;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class LessonPlanActivity extends Activity {

    private Cursor cursor;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_plan);
    }

    @Override
    protected void onStart() {
        super.onStart();

        RecyclerView recyclerView = findViewById(R.id.plan_recycler);

        try {
            SQLiteOpenHelper openHelper = new HelperDatabase(this);
            db = openHelper.getReadableDatabase();
            cursor = db.query("LESSON_PLAN",
                    SubjectPlan.subjectPlanOnCursor,
                    null, null, null, null,
                    "DAY, TIME_START");

            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            AdapterPlanCardView adapter = new AdapterPlanCardView(cursor, findViewById(R.id.plan_plan_null));
            recyclerView.setAdapter(adapter);

            adapter.setListener(new AdapterPlanCardView.Listener() {
                @Override
                public void onClick(int id) {
                    Intent intent = new Intent(getApplicationContext(), LessonPlanEditActivity.class);
                    intent.putExtra(LessonPlanEditActivity.EXTRA_ID, id);
                    startActivity(intent);
                }
            });
        }catch (SQLiteException e){
            Toast.makeText(this, R.string.error_database, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        cursor.close();
        db.close();
    }

    public void ClickAdd(View v){
        Intent intent = new Intent(this, LessonPlanEditActivity.class);
        startActivity(intent);
    }
}
