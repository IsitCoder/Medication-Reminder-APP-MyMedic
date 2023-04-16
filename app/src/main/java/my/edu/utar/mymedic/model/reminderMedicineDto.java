package my.edu.utar.mymedic.model;

public class reminderMedicineDto {
    int id;
    String medicineName;
    double dose;


    public reminderMedicineDto(int id, String medicineName, double dose) {
        this.id=id;
        this.medicineName = medicineName;
        this.dose = dose;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public double getDose() {
        return dose;
    }

    public int getId() {
        return id;
    }
    @Override
    public String toString() {
        return getMedicineName();
    }
}
