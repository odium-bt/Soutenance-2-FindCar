package fr.greta.soutenance2.service;

import fr.greta.soutenance2.exceptions.ScrapingException;
import fr.greta.soutenance2.modele.Vehicule;
import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlElement;
import org.htmlunit.html.HtmlPage;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ScrapingService extends Service {

    public ScrapingService() {
    }

    public List<Vehicule> search(int choix, String marque, String modele, String ville, double latitude, double longitude) throws IOException {
        return switch (choix) {
            case 1 -> leboncoinUrlBuilder(marque, modele, ville);
            case 2 -> lacentraleUrlBuilder(marque, modele, ville);
            case 3 -> autoscout24UrlBuilder(marque, modele, ville, latitude, longitude);
            default -> throw new IllegalStateException("Unexpected value : " + choix);
        };
    }


    private List<Vehicule> leboncoinUrlBuilder(String marque, String modele, String ville) {
        List<Vehicule> res = new ArrayList<>();

        String url = "https://www.leboncoin.fr/recherche?category=2&locations=" +
                ville +
                "&u_car_brand=" +
                marque +
                "&u_car_model=" +
                marque + "_" + modele;

        try (WebClient webClient = new WebClient()) {
            webClient.getOptions().setUseInsecureSSL(true);
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setJavaScriptEnabled(false);
            HtmlPage htmlPage = webClient.getPage(url);

            List<HtmlElement> annonces = htmlPage.getByXPath("//li[contains(@class, '_5obUyG_adCard')]");

            for (HtmlElement annonce : annonces) {
                HtmlElement article = annonce.getFirstByXPath(".//article");
                if (article == null) continue;

                HtmlElement titre = annonce.getFirstByXPath(".//p[contains(@class, 'text-body-1-highlight')]");
                String modeleAnnonce = titre == null ? "" : titre.getTextContent().trim();

                HtmlElement prixElement =
                        annonce.getFirstByXPath(".//div[@aria-hidden='true']//span[normalize-space()]");
                String prix = prixElement != null ? prixElement.getTextContent().trim() : "";
                String annee = getInfo(annonce, "Année");

                String kilometrage = getInfo(annonce, "Kilométrage");
                String energie = getInfo(annonce, "Énergie");
                String boite = getInfo(annonce, "Boîte de vitesse");

                HtmlElement image = annonce.getFirstByXPath(".//picture/img");
                String imageUrl = image != null ? image.getAttribute("src") : "";

                HtmlElement lieuElement = annonce.getFirstByXPath(
                        ".//p[@data-qa-id='pro-store-name']/following-sibling::p[1]");
                String lieu = lieuElement != null ? lieuElement.getTextContent().trim() : "";

                res.add(new Vehicule(modeleAnnonce, energie, kilometrage, prix, annee, lieu, boite));
            }
        } catch (IOException e) {
            throw new ScrapingException(e.getMessage());
        }

        return res;
    }

    private List<Vehicule> lacentraleUrlBuilder(String marque, String modele, String ville) {
        List<Vehicule> res = new ArrayList<>();

        // TODO : Obtenir le code postal
        String codePostal = "";

        String url = "https://www.lacentrale.fr/listing?dptCp=" + codePostal + "&makesModelsCommercialNames=" +
                marque + "%3A%3A" + modele;
        url = url.replace(" ", "%")
                .replace("_", "%3A%3A");

        try (WebClient webClient = new WebClient()) {
            webClient.getOptions().setUseInsecureSSL(true);
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setJavaScriptEnabled(false);

            HtmlPage htmlPage = webClient.getPage(url);

            // Récupération des annonces
            List<HtmlElement> annonces = htmlPage.getByXPath(
                    "//div[contains(@class, 'searchCard')]"
            );

            for (HtmlElement annonce : annonces) {
                HtmlElement titre = annonce.getFirstByXPath(".//h2[contains(@class, 'vehiclecardV2_title')]");
                String modeleAnnonce = titre != null ? titre.getTextContent().trim() : "";

                HtmlElement prixElement = annonce.getFirstByXPath(
                        ".//div[contains(@class, 'vehiclecardV2_vehiclePrice')]");
                String prix = prixElement != null ? prixElement.getTextContent().trim() : "";

                List<HtmlElement> caracteristiques = annonce.getByXPath(
                        ".//div[contains(@class, 'vehiclecardV2_vehicleCharacteristics')]//span[contains(@class, 'vehiclecardV2_vehicleCharacteristicsItem')]");

                String annee = "";
                String kilometrage = "";
                String energie = "";
                String boite = "";

                for (int i = 0; i < caracteristiques.size(); i++) {
                    String valeur = caracteristiques.get(i).getTextContent().trim();
                    switch (i) {
                        case 0 -> annee = valeur;
                        case 1 -> boite = valeur;
                        case 2 -> kilometrage = valeur;
                        case 3 -> energie = valeur;
                    }
                }

                HtmlElement image = annonce.getFirstByXPath(
                        ".//div[contains(@class, 'vehiclecardV2_pictureDesktop')]//img");
                String imageUrl = image != null ? image.getAttribute("src") : "";

                HtmlElement vendeurElement = annonce.getFirstByXPath(
                        ".//div[contains(@class, 'vehiclecardV2_seller__')]"
                );
                String store = vendeurElement != null
                        ? vendeurElement.getTextContent().trim()
                        : "";

                res.add(new Vehicule(modeleAnnonce, energie, kilometrage, prix, annee, store, boite));
            }

        } catch (IOException e) {
            throw new ScrapingException(e.getMessage());
        }

        return res;
    }


    private List<Vehicule> autoscout24UrlBuilder(String marque, String modele, String ville, double latitude, double longitude) {
        List<Vehicule> res = new ArrayList<>();

        String villeSlug = ville.toLowerCase().replace(" ", "_");

        String villeEncodee = URLEncoder.encode(ville, StandardCharsets.UTF_8);

        String url = "https://www.autoscout24.fr/lst/" +
                marque.toLowerCase() + "/" +
                modele.toLowerCase() +
                "/cit_" + villeSlug +
                "?lat=" + latitude +
                "&lon=" + longitude +
                "&cy=F" +
                "&damaged_listing=exclude" +
                "&desc=0" +
                "&powertype=kw" +
                "&sort=standard" +
                "&ustate=N%2CU" +
                "&zip=" + villeEncodee +
                "&zipr=200&atype=C";

        try (WebClient webClient = new WebClient()) {
            webClient.getOptions().setUseInsecureSSL(true);
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setJavaScriptEnabled(false);

            HtmlPage htmlPage = webClient.getPage(url);

            // Récupération des annonces
            List<HtmlElement> annonces = htmlPage.getByXPath(
                    "//article[@data-testid='list-item']"
            );

            for (HtmlElement annonce : annonces) {
                HtmlElement titre = annonce.getFirstByXPath(".//span[contains(@class, 'ListItemTitle_title')]");
                String modeleAnnonce = titre != null ? titre.getTextContent().trim() : "";

                HtmlElement prixElement = annonce.getFirstByXPath(".//span[@data-testid='regular-price']");
                String prix = prixElement != null ? prixElement.getTextContent().trim() : "";

                String annee = annonce.getAttribute("data-first-registration");
                String kilometrage = annonce.getAttribute("data-mileage");

                String energie = "";
                HtmlElement energieElement = annonce.getFirstByXPath(
                        ".//div[@data-testid='VehicleDetails-gas_pump']//span[contains(@class, 'ListItemPill_text')]");
                if (energieElement != null) {
                    energie = energieElement.getTextContent().trim();
                }

                // Le mode de transmission n'est pas affiché dans l'aperçu de l'annonce
                String boite = "";

                HtmlElement image = annonce.getFirstByXPath(".//img[@data-testid='list-item-image']");
                String imageUrl = image != null ? image.getAttribute("src") : "";

                HtmlElement lieuElement = annonce.getFirstByXPath(".//span[@data-testid='dealer-address']");
                String lieu = lieuElement != null ? lieuElement.getTextContent().trim().replaceFirst("^FR-\\d+\\s*", "") : "";

                res.add(new Vehicule(modeleAnnonce, energie, kilometrage, prix, annee, lieu, boite));
            }

        } catch (IOException e) {
            throw new ScrapingException(e.getMessage());
        }

        return res;
    }


    private String getInfo(HtmlElement annonce, String libelle) {
        HtmlElement element = annonce.getFirstByXPath(
                ".//div[contains(@class, 'adcard_84a5c3b0f')]" + "//div[p[1]='" + libelle + "']/p[2]");

        return element != null ? element.getTextContent().trim() : "";
    }
}
