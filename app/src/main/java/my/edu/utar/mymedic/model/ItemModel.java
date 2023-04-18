package my.edu.utar.mymedic.model;

public class ItemModel implements Comparable<ItemModel>{
    private  boolean isSectionHeader;
    private String itemTime;
    private String itemName;
    private String itemDose;
    private String date;

    public ItemModel(String itemTime, String itemName, String itemDose, String date) {
        this.itemTime = itemTime;
        this.itemName = itemName;
        this.itemDose = itemDose;
        this.date = date;
        isSectionHeader=false;
    }


    @Override
    public int compareTo(ItemModel itemModel) {
        return this.date.compareTo(itemModel.date);
    }

    public boolean isSectionHeader() {
        return isSectionHeader;
    }

    public void setSectionHeader(boolean sectionHeader) {
        isSectionHeader = sectionHeader;
    }

    public String getItemTime() {
        return itemTime;
    }

    public void setItemTime(String itemTime) {
        this.itemTime = itemTime;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setToSectionHeader() {
        isSectionHeader = true;
    }

    public String getItemDose() {
        return itemDose;
    }

    public void setItemDose(String itemDose) {
        this.itemDose = itemDose;
    }
}
