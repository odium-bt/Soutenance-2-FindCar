package fr.greta.soutenance2.modele;

public class VehiculeAnnonce {
    private String modele;
    private String energie;
    private String kms;
    private String prix;
    private String annee;
    private String lieu;
    private String transmission;
    private String photo;

    public VehiculeAnnonce(String modele, String energie, String kms, String prix, String annee, String transmission, String urlAnnonce, String photo) {
        this.modele = modele;
        this.energie = energie;
        this.kms = kms;
        this.prix = prix;
        this.annee = annee;
        this.lieu = "";
        this.transmission = transmission;
    }

    public VehiculeAnnonce(String modele, String kms, String prix, String annee, String lieu, String urlAnnonce, String photo) {
        this.modele = modele;
        this.energie = "";
        this.kms = kms;
        this.prix = prix;
        this.annee = annee;
        this.lieu = lieu;
        this.transmission = "";
    }

    public String getModele() {
        return modele;
    }

    public void setModele(String modele) {
        this.modele = modele;
    }

    public String getEnergie() {
        return energie;
    }

    public void setEnergie(String energie) {
        this.energie = energie;
    }

    public String getKms() {
        return kms;
    }

    public void setKms(String kms) {
        this.kms = kms;
    }

    public String getPrix() {
        return prix;
    }

    public void setPrix(String prix) {
        this.prix = prix;
    }

    public String getAnnee() {
        return annee;
    }

    public void setAnnee(String annee) {
        this.annee = annee;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Override
    public String toString() {
        return "VehiculeAnnonce{" +
                "modele='" + modele + '\'' +
                ", energie='" + energie + '\'' +
                ", kms='" + kms + '\'' +
                ", prix='" + prix + '\'' +
                ", annee='" + annee + '\'' +
                ", lieu='" + lieu + '\'' +
                ", transmission='" + transmission + '\'' +
                ", photo='" + photo + '\'' +
                '}';
    }
}
