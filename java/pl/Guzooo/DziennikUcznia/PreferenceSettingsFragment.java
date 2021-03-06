package pl.Guzooo.DziennikUcznia;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import java.util.ArrayList;
import java.util.Arrays;

public class PreferenceSettingsFragment extends PreferenceFragmentCompat{

    private <T extends Preference> T findPref(int id) {
        String stringId = getString(id);
        return findPreference(stringId);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_preference, rootKey);

        setTitles();
        setRewriteAssessment();
        setResetUnpreparedness();
        setTheme();
        setHardDarkTheme();
        setAverageToAssessment();
        setAverageToAssessmentObjects();
        setAverageToBelt();
        setDeleteObjects();
    }

    private void setTitles(){
        ArrayList<Preference> preferences = getTitles();
        setAccentToPreferencesIcon(preferences);
    }

    private void setRewriteAssessment(){
        Preference rewrite = findPref(R.string.ID_REWRITE_ASSESSMENT);
        rewrite.setOnPreferenceClickListener(getRewriteAssessmentClickListener());
    }

    private void setResetUnpreparedness(){
        Preference reset1 = findPref(R.string.ID_RESET_UNPREPAREDNESS_1);
        Preference reset2 = findPref(R.string.ID_RESET_UNPREPAREDNESS_2);
        reset1.setOnPreferenceClickListener(getResetUnpreparednessClickListener(1));
        reset2.setOnPreferenceClickListener(getResetUnpreparednessClickListener(2));
    }

    private void setTheme() {
        ListPreference theme = findPref(R.string.ID_THEME);
        if (theme.getValue() == null)
            theme.setValueIndex(2);
        theme.setOnPreferenceChangeListener(getThemeChangeListener());
    }

    private void setHardDarkTheme() {
        SwitchPreference hardDarkTheme = findPref(R.string.ID_HARD_DARK);
        hardDarkTheme.setOnPreferenceChangeListener(getHardDarkThemeChangeListener());
    }

    private void setAverageToAssessment(){
        SwitchPreference averageToAssessment = findPref(R.string.ID_AVERAGE_TO_ASSESSMENT);
        averageToAssessment.setOnPreferenceChangeListener(getAverageToAssessmentChangeListener());
    }

    private void setAverageToAssessmentObjects(){
        ArrayList<EditTextPreference> edits = getAverageToAssessmentObjects();
        for(EditTextPreference edit : edits){
            edit.setOnBindEditTextListener(getBindFloatEditText());
            edit.setOnPreferenceChangeListener(getDontSaveEmptyValueChangeListener());
        }
        boolean visibility = DataManager.isAverageToAssessment(getContext());
        setVisibilityAverageToAssessmentObjects(visibility);
    }

    private void setAverageToBelt(){
        EditTextPreference toBelt = findPref(R.string.ID_AVERAGE_TO_BELT);
        toBelt.setOnBindEditTextListener(getBindFloatEditText());
        toBelt.setOnPreferenceChangeListener(getDontSaveEmptyValueChangeListener());
    }

    private void setDeleteObjects(){
        ArrayList<Preference> preferences = getDelPreferences();
        ArrayList<Integer> plurals = getDelPlurals();
        ArrayList<String> tableNames = getDelTableNames();
        ArrayList<Integer> counts = getDelCounts(tableNames);
        ArrayList<String> manySummary = getDelSummary(plurals, counts);
        ArrayList<Preference.OnPreferenceClickListener> listeners = getDelClickListeners(preferences, tableNames, manySummary);
        setDelSummary(preferences, manySummary);
        setDelClickListener(preferences, listeners);
    }

    private ArrayList<Preference> getTitles(){
        ArrayList<Preference> preferences = new ArrayList<>();
        int count = getPreferenceScreen().getPreferenceCount();
        for(int i = 0; i < count; i++) {
            Preference preference = getPreferenceScreen().getPreference(i);
            preferences.add(preference);
        }
        return preferences;
    }

    private void setAccentToPreferencesIcon(ArrayList<Preference> preferences){
        for(Preference preference : preferences) {
            Resources.Theme theme = getContext().getTheme();
            TypedArray a = theme.obtainStyledAttributes(new int[]{R.attr.colorAccentG});
            int color = a.getColor(0, 0);
            if (UtilsFragmentation.isMinimumLollipop())
                preference.getIcon().setTint(color);
        }
    }

    private void setVisibilityAverageToAssessmentObjects(boolean visibility){
        for(EditTextPreference edit : getAverageToAssessmentObjects())
            edit.setVisible(visibility);
    }

    private ArrayList<EditTextPreference> getAverageToAssessmentObjects(){
        ArrayList<EditTextPreference> edits = new ArrayList<>();
        edits.add((EditTextPreference) findPref(R.string.ID_AVERAGE_TO_SIX));
        edits.add((EditTextPreference) findPref(R.string.ID_AVERAGE_TO_FIVE));
        edits.add((EditTextPreference) findPref(R.string.ID_AVERAGE_TO_FOUR));
        edits.add((EditTextPreference) findPref(R.string.ID_AVERAGE_TO_THREE));
        edits.add((EditTextPreference) findPref(R.string.ID_AVERAGE_TO_TWO));
        return edits;
    }

    private ArrayList<Preference> getDelPreferences(){
        return new ArrayList<>(Arrays.asList(
                findPref(R.string.ID_DEL_LESSON_PLAN),
                findPref(R.string.ID_DEL_NOTES),
                findPref(R.string.ID_DEL_ASSESSMENTS),
                findPref(R.string.ID_DEL_SUBJECTS)
        ));
    }

    private ArrayList<Integer> getDelPlurals(){
        return new ArrayList<>(Arrays.asList(
                R.plurals.summary_lesson_plan,
                R.plurals.summary_all_notes,
                R.plurals.summary_all_assessments,
                R.plurals.summary_all_subejcts
        ));
    }

    private ArrayList<String> getDelTableNames(){
        return new ArrayList<>(Arrays.asList(
                ElementOfPlan2020.DATABASE_NAME,
                Note2020.DATABASE_NAME,
                Assessment2020.DATABASE_NAME,
                Subject2020.DATABASE_NAME
        ));
    }

    private ArrayList<Integer> getDelCounts(ArrayList<String> tableNames){
        ArrayList<Integer> counts = new ArrayList<>();
        for(String string : tableNames){
            int count = Database2020.getTableCount(string, getContext());
            counts.add(count);
        }
        return counts;
    }

    private ArrayList<String> getDelSummary(ArrayList<Integer> plurals, ArrayList<Integer> counts){
        ArrayList<String> manySummary = new ArrayList<>();
        for(int i = 0; i < plurals.size(); i++){
            String summary = getSummary(plurals.get(i), counts.get(i));
            manySummary.add(summary);
        }
        String subjectSummary = manySummary.get(3);
        subjectSummary += getDelSubjectSummary();
        manySummary.set(3, subjectSummary);
        return manySummary;
    }

    private ArrayList<Preference.OnPreferenceClickListener> getDelClickListeners(ArrayList<Preference> preferences, ArrayList<String> tableNames, ArrayList<String> manySummary){
        ArrayList<Preference.OnPreferenceClickListener> listeners = new ArrayList<>();
        for(int i = 0; i < tableNames.size(); i++){
            String title = preferences.get(i).getTitle().toString().toLowerCase();
            Preference.OnPreferenceClickListener listener = getDelDialog(tableNames.get(i), title, manySummary.get(i));
            listeners.add(listener);
        }
        return listeners;
    }

    private void setDelSummary(ArrayList<Preference> preferences, ArrayList<String> manySummary){
        for(int i = 0; i < preferences.size(); i++){
            Preference preference = preferences.get(i);
            String summary = manySummary.get(i);
            preference.setSummary(summary);
        }
    }

    private void setDelClickListener(ArrayList<Preference> preferences, ArrayList<Preference.OnPreferenceClickListener> listeners){
        for(int i = 0; i < preferences.size(); i++){
            Preference preference = preferences.get(i);
            Preference.OnPreferenceClickListener listener = listeners.get(i);
            preference.setOnPreferenceClickListener(listener);
        }
    }

    private String getDelSubjectSummary(){
        ArrayList<String> tableNames = getDelSubjectTableNames();
        ArrayList<String> columnNames = getDelSubjectColumnNames();
        ArrayList<Integer> plurals = getDelSubjectPlurals();
        ArrayList<Integer> counts = getDelSubjectCounts(tableNames, columnNames);
        String summary = " " + getSummary(plurals.get(0), counts.get(0));
        summary += ", " + getSummary(plurals.get(1), counts.get(1));
        summary += ", " + getSummary(plurals.get(2), counts.get(2));
        summary += " " + getString(R.string.in_lesson_plan);
        return summary;
    }

    private ArrayList<String> getDelSubjectTableNames(){
        return new ArrayList<>(Arrays.asList(
                Assessment2020.DATABASE_NAME,
                Note2020.DATABASE_NAME,
                ElementOfPlan2020.DATABASE_NAME
        ));
    }

    private ArrayList<String> getDelSubjectColumnNames(){
        return new ArrayList<>(Arrays.asList(
                Assessment2020.TAB_SUBJECT,
                Note2020.TAB_SUBJECT,
                ElementOfPlan2020.TAB_SUBJECT
        ));
    }

    private ArrayList<Integer> getDelSubjectPlurals(){
        return new ArrayList<>(Arrays.asList(
                R.plurals.summary_all_assessments,
                R.plurals.summary_all_notes,
                R.plurals.summary_lesson_plan
        ));
    }

    private ArrayList<Integer> getDelSubjectCounts(ArrayList<String> tableNames, ArrayList<String> columnNames){
        ArrayList<Integer> counts = new ArrayList<>();
        for(int i = 0; i < tableNames.size(); i++){
            String tableName = tableNames.get(i);
            String columnName = columnNames.get(i);
            int count = Database2020.getTableCountOnlySubjectElement(tableName, columnName, getContext());
            counts.add(count);
        }
        return counts;
    }

    private String getSummary(int plurals, int variable){
        return getResources().getQuantityString(plurals, variable, variable);
    }

    private Preference.OnPreferenceClickListener getRewriteAssessmentClickListener(){
        return new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.title_rewrite_assessment)
                        .setMessage(R.string.summary_rewrite_assessment)
                        .setPositiveButton(R.string.rewrite, getRewriteAssessmentDoListener())
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
                return true;
            }
        };
    }

    private DialogInterface.OnClickListener getRewriteAssessmentDoListener(){
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SQLiteDatabase db = Database2020.getToWriting(getContext());
                Cursor cursor = db.query(Assessment2020.DATABASE_NAME,
                        Assessment2020.ON_CURSOR,
                        Assessment2020.SEMESTER + " = ?",
                        new String[]{Integer.toString(1)},
                        null, null, null);
                if(cursor.moveToFirst())
                    do{
                        Assessment2020 assessment = new Assessment2020();
                        assessment.setVariablesOfCursor(cursor);
                        assessment.setSemester(2);
                        assessment.insert(getContext());
                    }while(cursor.moveToNext());
                cursor.close();
                db.close();
                Toast.makeText(getContext(), R.string.done_rewrite_assessment, Toast.LENGTH_SHORT).show();
            }
        };
    }

    private Preference.OnPreferenceClickListener getResetUnpreparednessClickListener(final int semester){
        return new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.title_reset_unpreparedness)
                        .setMessage(R.string.reset_unpreparedness_description)
                        .setPositiveButton(R.string.reset, getResetUnpreparednessDoListener(semester))
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
                return true;
            }
        };
    }

    private DialogInterface.OnClickListener getResetUnpreparednessDoListener(final int semester){
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SQLiteDatabase db = Database2020.getToWriting(getContext());
                ContentValues contentValues = getResetUnpreparednessContentValues(semester);
                db.update(Subject2020.DATABASE_NAME, contentValues, null, null);
                db.close();
                Toast.makeText(getContext(), R.string.done_reset_unpreparedness, Toast.LENGTH_SHORT).show();
            }
        };
    }

    private ContentValues getResetUnpreparednessContentValues(int semester){
        ContentValues contentValues = new ContentValues();
        if(semester == 1)
            contentValues.put(Subject2020.UNPREPAREDNESS1, -1);
        else
            contentValues.put(Subject2020.UNPREPAREDNESS2, -1);
        return contentValues;
    }

    private Preference.OnPreferenceChangeListener getThemeChangeListener() {
        return new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String value = newValue.toString();
                int nightMode = Integer.valueOf(value);
                AppCompatDelegate.setDefaultNightMode(nightMode);
                return true;
            }
        };
    }

    private Preference.OnPreferenceChangeListener getHardDarkThemeChangeListener(){
        return new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int currentNightMode = getContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
                if(currentNightMode == Configuration.UI_MODE_NIGHT_YES)
                    getActivity().recreate();
                return true;
            }
        };
    }

    private Preference.OnPreferenceChangeListener getAverageToAssessmentChangeListener(){
        return new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String value = newValue.toString();
                boolean bool = Boolean.valueOf(value);
                setVisibilityAverageToAssessmentObjects(bool);
                return true;
            }
        };
    }

    private EditTextPreference.OnBindEditTextListener getBindFloatEditText(){
        return new EditTextPreference.OnBindEditTextListener() {
            @Override
            public void onBindEditText(EditText editText) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                editText.setSelection(editText.length());
            }
        };
    }

    private Preference.OnPreferenceChangeListener getDontSaveEmptyValueChangeListener(){
        return new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                return !newValue.toString().isEmpty();
            }
        };
    }

    private Preference.OnPreferenceClickListener getDelDialog(final String tableName, final String titleDel, final String question){
        return new Preference.OnPreferenceClickListener() {
            String title = getString(R.string.settings_delete) + " " + titleDel;
            String message = getString(R.string.del_question, question);
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new AlertDialog.Builder(getContext())
                        .setTitle(title)
                        .setMessage(message)
                        .setPositiveButton(android.R.string.yes, getDeleteListener(tableName))
                        .setNegativeButton(android.R.string.no, null)
                        .show();
                return true;
            }
        };
    }

    private DialogInterface.OnClickListener getDeleteListener(final String tableName){
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(tableName.equals(Subject2020.DATABASE_NAME))
                    Database2020.delAllSubjects(getContext());
                else
                    Database2020.delAllTable(tableName, getContext());
                refreshBeforeDeleteTable();
            }
        };
    }

    private void refreshBeforeDeleteTable(){
        setDeleteObjects();
    }
}