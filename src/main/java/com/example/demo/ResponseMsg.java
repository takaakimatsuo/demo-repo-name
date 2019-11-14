package com.example.demo;

public class ResponseMsg{
        response_status rs;
        String msg;

        public ResponseMsg(response_status s, String msg){
                this.rs = s;
                this.msg = msg;
        }

        public ResponseMsg(){
                this.rs = response_status.OK;
                this.msg = "Blank message";
        }

        public response_status getRS() {
                return rs;
        }
        public void setRS(response_status rs) {
                this.rs = rs;
        }

        public String getMsg() {
                return msg;
        }
        public void setMsg(String msg) {
                this.msg = msg;
        }

}
