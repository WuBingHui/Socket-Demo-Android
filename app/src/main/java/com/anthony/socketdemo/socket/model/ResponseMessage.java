package com.anthony.socketdemo.socket.model;

public class ResponseMessage {

    public static final int _TYPE_MESSAGE = 0;
    public static final int _TYPE_LOG = 1;
    public static final int _TYPE_ACTION = 2;

    private int type;
    private String message;
    private String username;


    private ResponseMessage() {}

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public static class Builder {
        private final int mType;
        private String mUsername;
        private String mMessage;

        public Builder(int type) {
            mType = type;
        }

        public Builder username(String username) {
            mUsername = username;
            return this;
        }

        public Builder message(String message) {
            mMessage = message;
            return this;
        }

        public ResponseMessage build() {
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.type = mType;
            responseMessage.username = mUsername;
            responseMessage.message = mMessage;
            return responseMessage;
        }
    }




}
