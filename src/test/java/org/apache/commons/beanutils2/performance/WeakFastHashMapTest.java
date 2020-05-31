/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.beanutils2.performance;

import org.apache.commons.beanutils2.WeakFastHashMap;
import org.apache.commons.logging.impl.WeakHashtable;

import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.TreeSet;


public class WeakFastHashMapTest {
    static final TreeSet<Integer> treeSet = new TreeSet<>();
    static final Random random = new Random();
    static final WeakFastHashMap<Integer, Integer> weakFastHashMap = new WeakFastHashMap<>();
    static final Map<Integer, Integer> weakHashtable = new WeakHashtable();

    static final int INIT_SIZE = 50000;
    static final double WRITE_CHANCE = 0.01;
    static final double READ_NON_EXIST_CHANCE = 0.30;

    public static void main(String[] args) {
        for (int i = 0; i < INIT_SIZE; i++) {
            writeRandom();
        }
        System.out.println("init over");

        weakFastHashMap.setFast(true);

        for (int i = 0; ; i++) {
            if (i % 100000 == 0) {
                System.out.println("running> i : " + i + " size: " + treeSet.size());
            }
            double ifWrite = random.nextDouble();
            if (ifWrite < WRITE_CHANCE) {
                writeRandom();
            } else {
                double ifNonExist = random.nextDouble();
                if (ifNonExist < READ_NON_EXIST_CHANCE) {
                    readNonExist();
                } else {
                    readExist();
                }
            }
        }
    }

    public static void writeRandom() {
        Integer nowKey = random.nextInt() * random.nextInt();
        Integer nowValue = random.nextInt() * random.nextInt();
        treeSet.add(nowKey);
        weakFastHashMap.put(nowKey, nowValue);
        weakHashtable.put(nowKey, nowValue);
    }

    public static void readExist() {
        Integer nowKey = null;
        while (nowKey == null) {
            nowKey = treeSet.lower(random.nextInt() * random.nextInt());
        }
        read(nowKey);
    }

    public static void readNonExist() {
        Integer nowKey = random.nextInt() * random.nextInt();
        while (treeSet.contains(nowKey)) {
            nowKey = random.nextInt() * random.nextInt();
        }
        read(nowKey);
    }

    public static void read(Integer nowKey) {
        Integer value1 = weakFastHashMap.get(nowKey);
        Integer value2 = weakHashtable.get(nowKey);
        if (!Objects.equals(value1, value2)) {
            System.out.println("not equal!  nowKey : " + nowKey + " value1 : " + value1 + " value2 : " + value2);
        }
    }
}
