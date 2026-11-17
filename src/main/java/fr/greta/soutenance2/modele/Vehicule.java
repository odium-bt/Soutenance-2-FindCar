package fr.greta.soutenance2.modele;

public class Vehicule {
    private final String modele;
    private final String energie;
    private final String kms;
    private final String prix;
    private final String annee;
    private final String lieu;
    private final String transmission;
    private String photo;

    public Vehicule(String modele, String energie, String kms, String prix, String annee, String lieu, String transmission) {
        this.modele = modele;
        this.energie = energie;
        this.kms = kms;
        this.prix = prix;
        this.annee = annee;
        this.lieu = lieu;
        this.transmission = transmission;
    }

    public Vehicule(String modele, String energie, String kms, String prix, String annee, String lieu, String transmission, String photo) {
        this.modele = modele;
        this.energie = energie;
        this.kms = kms;
        this.prix = prix;
        this.annee = annee;
        this.lieu = lieu;
        this.transmission = transmission;
        this.photo = photo;
    }

    public String getModele() {
        return modele;
    }

    public String getEnergie() {
        return energie;
    }

    public String getKms() {
        return kms;
    }

    public String getPrix() {
        return prix;
    }

    public String getAnnee() {
        return annee;
    }

    public String getLieu() {
        return lieu;
    }

    public String getTransmission() {
        return transmission;
    }

    public String getPhoto() {
        return photo;
    }

    @Override
    public String toString() {
        return "Vehicule{" +
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
