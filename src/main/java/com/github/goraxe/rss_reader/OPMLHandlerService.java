package com.github.goraxe.rss_reader;

import org.springframework.stereotype.Service;

/**
 * Created by goraxe on 2016-03-30.
 * Boo Yar
 */
@SuppressWarnings("EmptyMethod")
@Service
public class OPMLHandlerService {

    OPMLHandler getOPMLHanlder() {
        return new OPMLHandler();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
