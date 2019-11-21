package DemoBackend.CustomObjects;

import DemoBackend.CustomENUMs.response_status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



public class ResponseHeader {
        private response_status status;
        private String message;


        public ResponseHeader(response_status rs, String msg){
                this.status = rs;
                this.message = msg;
        }

        public ResponseHeader(){
                this.status = response_status.OK;
                this.message = "Blank message";
        }

        public response_status getStatus() {
                return status;
        }

        public void setStatus(response_status rs) {
                this.status = rs;
        }

        public String getMessage() {
                return message;
        }

        public void setMessage(String msg) {
                this.message = msg;
        }

}
