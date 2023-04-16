package my.edu.utar.mymedic.model;

public class medicineDto {
    int id;
    String medicineName;
    double dose;
    double remainVolume;


    public medicineDto(int id,String medicineName, double dose, double remainVolume) {
        this.id=id;
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

    public int getId() {
        return id;
    }
}
