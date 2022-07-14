package com.example.mystoryapp;

public class Constants {

    public enum AlertType{

        // Used to define the type of AlertDialog to use
        ALERT_CANCEL(0),
        ALERT_FINISH(1);

        AlertType(int i) {
            this.i = i;
        }

        private int i;

        public int getInt() {
            return i;
        }

        public void setInt(int i) {
            this.i = i;
        }
    }
}
