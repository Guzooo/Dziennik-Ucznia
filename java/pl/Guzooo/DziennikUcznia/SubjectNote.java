package pl.Guzooo.DziennikUcznia;

public class SubjectNote {
    private String name;
    private String note;

    SubjectNote(String name, String note){
        setName(name);
        setNote(note);
    }

    SubjectNote(String object) {
        String[] strings = object.split("®");
        setName(strings[0]);
        setNote(strings[1]);
    }

    @Override
    public String toString() {
        String string = getName() + "®" + getNote() + "®" + "®" + "®" + "®" + "®" + "®" + "®" + "®" + "®";
        return string;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setNote(String note){
        this.note = note;
    }

    public String getNote() {
        return note;
    }
}
