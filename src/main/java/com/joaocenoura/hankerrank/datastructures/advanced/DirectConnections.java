package com.joaocenoura.hankerrank.datastructures.advanced;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author João Rodrigues <jlrodrigues.dev@gmail.com>
 */
public class DirectConnections {

    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);

        // number of test cases
        int t = in.nextInt();
        for (int i = 0; i < t; i++) {
            // N defines the number of cities
            int n = in.nextInt();

            List<City> cities = new ArrayList<>();

            // first line has each city position
            for (int j = 0; j < n; j++) {
                City city = new City();
                city.setX(in.nextInt());
                cities.add(city);
            }

            // second line has each city population
            for (int j = 0; j < n; j++) {
                City city = cities.get(j);
                city.setPop(in.nextInt());
            }

            // sort by population (high population first)
            Collections.sort(cities, new CityByPopComparator());

            long result = 0L;
            for (int j = 0; j < n; j++) {
                for (int k = j + 1; k < n; k++) {
                    City cityHigh = cities.get(j);
                    City cityLow = cities.get(k);
                    result += cityHigh.calculateNeededCable(cityLow);
                    result %= 1000000007;
                }
            }

            System.out.println(result);
        }
    }

    private static class CityByPopComparator implements Comparator<City> {

        @Override
        public int compare(City o1, City o2) {
            if (o1.getPop() > o2.getPop()) {
                return -1;
            } else if (o1.getPop() < o2.getPop()) {
                return 1;
            }
            return 0;
        }

    }

    private static class City {

        private int x;
        private int pop;

        public City() {
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getPop() {
            return pop;
        }

        public void setPop(int pop) {
            this.pop = pop;
        }

        public int getDistanceFrom(City c) {
            if (x < c.getX()) {
                return c.getX() - x;
            }
            return x - c.getX();
        }

        public long calculateNeededCable(City c) {
            int distance = getDistanceFrom(c);
            long result = (long) pop * distance;
            return result;
        }

        @Override
        public String toString() {
            return "[" + x + "] " + pop;
        }
    }
}
