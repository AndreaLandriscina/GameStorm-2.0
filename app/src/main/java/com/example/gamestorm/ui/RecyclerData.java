package com.example.gamestorm.ui;

public class RecyclerData {
        private final int id;
        private final String imgUrl;
        //private String name;

        public RecyclerData(int id, String imgUrl) {
            this.id = id;
            this.imgUrl = imgUrl;
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public int getId() {
            return id;
        }
}
