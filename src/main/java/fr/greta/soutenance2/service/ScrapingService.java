package fr.greta.soutenance2.service;

import fr.greta.soutenance2.exceptions.ScrapingException;
import fr.greta.soutenance2.modele.VehiculeAnnonce;
import org.htmlunit.BrowserVersion;
import org.htmlunit.HttpMethod;
import org.htmlunit.WebClient;
import org.htmlunit.WebRequest;
import org.htmlunit.html.HtmlElement;
import org.htmlunit.html.HtmlPage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class ScrapingService extends Service {
    private final WebClient webClient;

    public ScrapingService() {
        BrowserVersion browser = new BrowserVersion.BrowserVersionBuilder(BrowserVersion.CHROME).build();
        WebClient webClient = new WebClient(browser);
        webClient.getOptions().setTimeout(1000);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setUseInsecureSSL(true);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        this.webClient = webClient;
    }

    public List<VehiculeAnnonce> search(int choix, String marque, String modele, String ville) throws IOException {
        return switch (choix) {
            case 1 -> distinxionListBuilder(marque, modele);
            case 2 -> guercoetautoListBuilder(marque, modele);
            default -> throw new IllegalStateException("Unexpected value : " + choix);
        };
    }

    private List<VehiculeAnnonce> distinxionListBuilder(String marque, String modele) {
        List<VehiculeAnnonce> res = new ArrayList<>();

        String url = "https://www.distinxion.fr/voitures/" + marque.toLowerCase() + "/" + modele.toLowerCase();

        try (webClient) {
            URI uri = new URI(url);
            WebRequest wr = new WebRequest(uri.toURL(), HttpMethod.GET);

            wr.setAdditionalHeader("Content-Type", "application/x-www-form-urlencoded");
            wr.setAdditionalHeader("Origin", "https://www.leboncoin.fr/");
            wr.setAdditionalHeader("Referer", "https://www.leboncoin.fr/");

            HtmlPage htmlPage = webClient.getPage(wr);

            logRequestResult(htmlPage);

            // Récupération des annonces
            List<HtmlElement> annonces = htmlPage.getByXPath("//a[contains(@class, 'card') and starts-with(@href, '/voitures/')]");

            for (HtmlElement annonce : annonces) {
                HtmlElement titreElement = annonce.getFirstByXPath(".//div[contains(@class, 'card-title')]");
                String modeleAnnonce = titreElement != null ? titreElement.asNormalizedText().trim().replace('\u00A0', ' ') : "";

                HtmlElement caracteristiquesElement = annonce.getFirstByXPath(".//p[contains(@class, 'card-text')]");
                String[] caracteristiques = caracteristiquesElement != null ? caracteristiquesElement.asNormalizedText().trim().split("\\s*-\\s*") : new String[0];
                String kilometrage = caracteristiques.length > 0 ? caracteristiques[0].trim() : "";
                String annee = caracteristiques.length > 1 ? caracteristiques[1].trim() : "";
                String energie = caracteristiques.length > 2 ? caracteristiques[2].trim() : "";
                String transmission = caracteristiques.length > 3 ? caracteristiques[3].trim() : "";

                HtmlElement prixElement = annonce.getFirstByXPath(".//div[contains(@class, 'card-footer')]//span[contains(@class, 'fw-semibold')]");
                String prix = prixElement != null ? prixElement.asNormalizedText().trim() : "";

                HtmlElement imageElement = annonce.getFirstByXPath(".//img");
                String image = imageElement != null ? imageElement.getAttribute("src") : "";

                String href = annonce.getAttribute("href");
                String urlAnnonce = "https://www.distinxion.fr" + href;

                res.add(new VehiculeAnnonce(modeleAnnonce, energie, kilometrage, prix, annee, transmission, urlAnnonce, image));
            }
        } catch (IOException | URISyntaxException e) {
            throw new ScrapingException(e.getMessage());
        }
        return res;
    }

    // TODO
    private List<VehiculeAnnonce> guercoetautoListBuilder(String marque, String modele) {
        List<VehiculeAnnonce> res = new ArrayList<>();

        String url = "https://www.distinxion.fr/voitures/" + marque.toLowerCase() + "/" + modele.toLowerCase();

        try (webClient) {
            URI uri = new URI(url);
            WebRequest wr = new WebRequest(uri.toURL(), HttpMethod.GET);

            wr.setAdditionalHeader("Content-Type", "application/x-www-form-urlencoded");
            wr.setAdditionalHeader("Origin", "https://www.distinxion.fr/");
            wr.setAdditionalHeader("Referer", "https://www.distinxion.fr/");

            HtmlPage htmlPage = webClient.getPage(wr);

            logRequestResult(htmlPage);

            // Récupération des annonces
            List<HtmlElement> annonces = htmlPage.getByXPath("//div[contains(@class, 'panel') and starts-with(@id, 'post-')]");

            for (HtmlElement annonce : annonces) {

                HtmlElement modeleElement = annonce.getFirstByXPath(".//div[contains(@class, 'infomarque')]");
                String modeleAnnonce = modeleElement != null ? modeleElement.getTextContent().trim() : "";

                HtmlElement infosElement = annonce.getFirstByXPath(".//div[contains(@class, 'infokil')]");
                String annee = "";
                String kilometrage = "";
                if (infosElement != null) {
                    String[] infos = infosElement.getTextContent().trim().split("\\s*-\\s*");
                    annee = infos.length > 0 ? infos[0].trim() : "";
                    kilometrage = infos.length > 1 ? infos[1].trim() : "";
                }

                HtmlElement prixElement = annonce.getFirstByXPath(".//div[contains(@class, 'infoprice')]");
                String prix = prixElement != null ? prixElement.getTextContent().trim() : "";

                HtmlElement imageElement = annonce.getFirstByXPath(".//img");
                String image = imageElement != null ? imageElement.getAttribute("src") : "";

                HtmlElement lienElement = annonce.getFirstByXPath(".//a");
                String urlAnnonce = lienElement != null ? lienElement.getAttribute("href") : "";

                res.add(new VehiculeAnnonce(modeleAnnonce, kilometrage, prix, annee, "ETS GUER COET AUTOMOBILES", urlAnnonce, image));
            }
        } catch (IOException | URISyntaxException e) {
            throw new ScrapingException(e.getMessage());
        }
        return res;
    }

    /**
     * Affiche le statut de la requête HTTP.
     *
     * @param htmlPage Page HTML transmise par la requête
     */
    private void logRequestResult(HtmlPage htmlPage) {
        log.info("Status HTTP : {}", htmlPage.getWebResponse().getStatusCode());
        log.info("URL : {}", htmlPage.getUrl());
        log.debug("Réponse : {}", htmlPage.asNormalizedText());
    }

    /* TODO Réimplémenter les fonctions si les sites ne sont pas bloquants
    * Fonctions des autres sites

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
     */

}
