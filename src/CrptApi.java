
import org.apache.commons.lang3.concurrent.TimedSemaphore;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CrptApi extends Thread {
    // The semaphore for limiting database load.
    private final TimedSemaphore semaphore;

    private TimeUnit timeUnit;

    private long duration;

    int requestLimit;

    private final String url = "https://ismp.crpt.ru/api/v3/lk/documents/create";
    // Create an instance and set the semaphore
    public CrptApi( TimeUnit timeUnit, int requestLimit, int duration) {
        this.duration =  duration;
        semaphore = new TimedSemaphore(duration, timeUnit, requestLimit);
    }

    // Gather statistics
    public void run() {
        try {
            while (semaphore.getAvailablePermits() > 0) {
                Thread.sleep(1000);
                semaphore.acquire();   // limit load
                requestToApi();        // issue a query
                System.out.println("Available permits: " + semaphore.getAvailablePermits() + " for "
                + Thread.currentThread().getName());
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {

        CrptApi statisticsThread = new CrptApi(TimeUnit.SECONDS, 5, 6);
        CrptApi statisticsThread2 = new CrptApi(TimeUnit.SECONDS, 1, 4);
        statisticsThread.start();
        statisticsThread2.start();

        try {
            statisticsThread.join();
            statisticsThread2.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized void requestToApi() throws RestClientException {
        System.out.println("API is used by thread: " + Thread.currentThread().getName());
        RestTemplate restTemplate = new RestTemplate();
        Request request = new Request();
        HttpEntity<Request> responseHttpEntity = new HttpEntity<>(request);
        System.out.println("Request to some api");
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Arrays.asList(MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON));
        restTemplate.getMessageConverters().add(0, converter);
        ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.POST, responseHttpEntity, Object.class);
    }

    static class Request {
        private String participantInn;
        private String doc_id;
        private String doc_status;
        private String doc_type;
        private Boolean importRequest;
        private String owner_inn;
        private String participant_inn;
        private String producer_inn;
        private Date date;
        private String production_type;
        private List<Products> products;
        private Date reg_date;
        private String reg_number;

        public Request(String participantInn, String doc_id, String doc_status, String doc_type, Boolean importRequest, String owner_inn, String participant_inn, String producer_inn, Date date, String production_type, List<Products> products, Date reg_date, String reg_number) {
            this.participantInn = participantInn;
            this.doc_id = doc_id;
            this.doc_status = doc_status;
            this.doc_type = doc_type;
            this.importRequest = importRequest;
            this.owner_inn = owner_inn;
            this.participant_inn = participant_inn;
            this.producer_inn = producer_inn;
            this.date = date;
            this.production_type = production_type;
            this.products = products;
            this.reg_date = reg_date;
            this.reg_number = reg_number;
        }

        public Request() {
        }

        public String getParticipantInn() {
            return participantInn;
        }

        public void setParticipantInn(String participantInn) {
            this.participantInn = participantInn;
        }

        public String getDoc_id() {
            return doc_id;
        }

        public void setDoc_id(String doc_id) {
            this.doc_id = doc_id;
        }

        public String getDoc_status() {
            return doc_status;
        }

        public void setDoc_status(String doc_status) {
            this.doc_status = doc_status;
        }

        public String getDoc_type() {
            return doc_type;
        }

        public void setDoc_type(String doc_type) {
            this.doc_type = doc_type;
        }

        public Boolean getImportRequest() {
            return importRequest;
        }

        public void setImportRequest(Boolean importRequest) {
            this.importRequest = importRequest;
        }

        public String getOwner_inn() {
            return owner_inn;
        }

        public void setOwner_inn(String owner_inn) {
            this.owner_inn = owner_inn;
        }

        public String getParticipant_inn() {
            return participant_inn;
        }

        public void setParticipant_inn(String participant_inn) {
            this.participant_inn = participant_inn;
        }

        public String getProducer_inn() {
            return producer_inn;
        }

        public void setProducer_inn(String producer_inn) {
            this.producer_inn = producer_inn;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public String getProduction_type() {
            return production_type;
        }

        public void setProduction_type(String production_type) {
            this.production_type = production_type;
        }

        public List<Products> getProducts() {
            return products;
        }

        public void setProducts(List<Products> products) {
            this.products = products;
        }

        public Date getReg_date() {
            return reg_date;
        }

        public void setReg_date(Date reg_date) {
            this.reg_date = reg_date;
        }

        public String getReg_number() {
            return reg_number;
        }

        public void setReg_number(String reg_number) {
            this.reg_number = reg_number;
        }
    }

    static class Products {
        private String certificate_document;
        private Date certificate_document_date;
        private String certificate_document_number;
        private String owner_inn;
        private String producer_inn;
        private Date production_date;
        private String tnved_code;
        private String uit_code;
        private String uitu_code;

        public Products(String certificate_document, Date certificate_document_date, String certificate_document_number, String owner_inn, String producer_inn, Date production_date, String tnved_code, String uit_code, String uitu_code) {
            this.certificate_document = certificate_document;
            this.certificate_document_date = certificate_document_date;
            this.certificate_document_number = certificate_document_number;
            this.owner_inn = owner_inn;
            this.producer_inn = producer_inn;
            this.production_date = production_date;
            this.tnved_code = tnved_code;
            this.uit_code = uit_code;
            this.uitu_code = uitu_code;
        }

        public String getCertificate_document() {
            return certificate_document;
        }

        public void setCertificate_document(String certificate_document) {
            this.certificate_document = certificate_document;
        }

        public Date getCertificate_document_date() {
            return certificate_document_date;
        }

        public void setCertificate_document_date(Date certificate_document_date) {
            this.certificate_document_date = certificate_document_date;
        }

        public String getCertificate_document_number() {
            return certificate_document_number;
        }

        public void setCertificate_document_number(String certificate_document_number) {
            this.certificate_document_number = certificate_document_number;
        }

        public String getOwner_inn() {
            return owner_inn;
        }

        public void setOwner_inn(String owner_inn) {
            this.owner_inn = owner_inn;
        }

        public String getProducer_inn() {
            return producer_inn;
        }

        public void setProducer_inn(String producer_inn) {
            this.producer_inn = producer_inn;
        }

        public Date getProduction_date() {
            return production_date;
        }

        public void setProduction_date(Date production_date) {
            this.production_date = production_date;
        }

        public String getTnved_code() {
            return tnved_code;
        }

        public void setTnved_code(String tnved_code) {
            this.tnved_code = tnved_code;
        }

        public String getUit_code() {
            return uit_code;
        }

        public void setUit_code(String uit_code) {
            this.uit_code = uit_code;
        }

        public String getUitu_code() {
            return uitu_code;
        }

        public void setUitu_code(String uitu_code) {
            this.uitu_code = uitu_code;
        }
    }
}