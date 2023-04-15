package my.edu.utar.mymedic.model;

public class medicineDto {
    String medicineName;
    double dose;
    double remainVolume;


    public medicineDto(String medicineName, double dose, double remainVolume) {
        this.medicineName = medicineName;
        this.dose = dose;
        this.remainVolume = remainVolume;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public double getDose() {
        return dose;
    }

    public double getRemainVolume() {
        return remainVolume;
    }
}
