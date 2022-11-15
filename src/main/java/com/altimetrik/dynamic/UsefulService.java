package com.altimetrik.dynamic;

import org.springframework.stereotype.Service;

@Service
class UsefulService {

    String doSomethingUseful(String resource) {
        return "resource=" + resource;
    }
}
