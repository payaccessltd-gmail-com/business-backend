package com.jamub.payaccess.api.exception;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@Component
public class PayAccessAuthException extends Exception/*Throwable implements ResponseErrorHandler*/ {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
//    private String errorMessage = null;


    public PayAccessAuthException(String message)
    {
        super(message);
    }

//    @Override
//    public boolean hasError(ClientHttpResponse httpResponse)
//            throws IOException {
//
//        return (
//                httpResponse.getStatusCode().series() == CLIENT_ERROR
//                        || httpResponse.getStatusCode().series() == SERVER_ERROR);
//    }
//
//    @SneakyThrows
//    @Override
//    public void handleError(ClientHttpResponse httpResponse)
//            throws IOException {
//
//        if (httpResponse.getStatusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR)) {
//            // handle SERVER_ERROR
//            throw new Exception("Internal Server error");
//        } else if (httpResponse.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
//            InputStream is = httpResponse.getBody();
//
//            List<String> text = new BufferedReader(
//                    new InputStreamReader(is, StandardCharsets.UTF_8))
//                    .lines()
//                    .collect(Collectors.toList());
//
//            logger.info("{}", text.size());
//            throw new Exception(text.get(0));
//        }
//    }
}
