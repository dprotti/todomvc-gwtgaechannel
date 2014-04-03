package com.todomvc;

import com.todomvc.shared.model.HasID;

public class TestUtil {

    public static class BasicHasID implements HasID {

        private final String id;

        public BasicHasID(String id) {
            this.id = id;
        }

        @Override
        public String getId() {
            return id;
        }

    }

}
