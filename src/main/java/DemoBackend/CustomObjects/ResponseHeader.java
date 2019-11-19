package DemoBackend.CustomObjects;

import DemoBackend.CustomENUMs.response_status;

public class ResponseHeader {
        response_status status;
        String message;

        public ResponseHeader(response_status rs, String msg){
                this.status = rs;
                this.message = msg;
        }

        public ResponseHeader(){
                this.status = response_status.OK;
                this.message = "Blank message";
        }

        public response_status getRS() {
                return status;
        }
        public void setRS(response_status rs) {
                this.status = rs;
        }

        public String getMsg() {
                return message;
        }
        public void setMsg(String msg) {
                this.message = msg;
        }

}
