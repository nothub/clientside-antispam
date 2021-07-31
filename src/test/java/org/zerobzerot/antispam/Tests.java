package org.zerobzerot.antispam;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

class Tests {

    @Test
    public void download() {
        final Set<String> bots = Main.download();
        Assertions.assertFalse(bots.isEmpty());
        bots.forEach(System.out::println);
    }

}
