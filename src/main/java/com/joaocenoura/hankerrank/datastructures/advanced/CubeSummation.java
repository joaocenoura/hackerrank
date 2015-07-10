package com.joaocenoura.hankerrank.datastructures.advanced;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author João Rodrigues <jlrodrigues.dev@gmail.com>
 */
public class CubeSummation {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        // number of test cases
        int t = in.nextInt();
        for (int i = 0; i < t; i++) {
            // N defines the N * N * N matrix. 
            int n = in.nextInt();
            // M defines the number of operations. 
            int m = in.nextInt();

            Cube c = new Cube(n);
            for (int j = 0; j < m; j++) {
                String operation = in.next();
                switch (operation) {
                    case "UPDATE": {
                        int x = in.nextInt() - 1;
                        int y = in.nextInt() - 1;
                        int z = in.nextInt() - 1;
                        int w = in.nextInt();
                        c.update(x, y, z, w);
                        break;
                    }
                    case "QUERY": {
                        int x1 = in.nextInt() - 1;
                        int y1 = in.nextInt() - 1;
                        int z1 = in.nextInt() - 1;
                        int x2 = in.nextInt() - 1;
                        int y2 = in.nextInt() - 1;
                        int z2 = in.nextInt() - 1;
                        c.query(x1, y1, z1, x2, y2, z2);
                        break;
                    }
                    default: {
                        throw new IllegalStateException("Unknown operation '" + operation + "'");
                    }
                }
            }
        }
    }

    /**
     * Cube representation
     */
    private static class Cube {

        Map<String, Cell> filledCells;

        public Cube(int n) {
            filledCells = new HashMap<>();
        }

        public String computeKey(int x, int y, int z) {
            return new StringBuilder(11)
                    .append(x).append("_")
                    .append(y).append("_")
                    .append(z).toString();
        }

        private void update(int x, int y, int z, int w) {
            String key = computeKey(x, y, z);
            Cell c = filledCells.get(key);
            if (c == null) {
                c = new Cell(x, y, z);
                filledCells.put(key, c);
            }
            c.setW(w);
        }

        private void query(int x1, int y1, int z1, int x2, int y2, int z2) {
            long result = 0;
            for (Cell c : filledCells.values()) {
                if (c.isWithinQuery(x1, y1, z1, x2, y2, z2)) {
                    result += c.getW();
                }
            }
            System.out.println(result);
        }

        private class Cell {

            private final int x;
            private final int y;
            private final int z;
            private long w;

            public Cell(int x, int y, int z) {
                this.x = x;
                this.y = y;
                this.z = z;
                this.w = 0;
            }

            public void setW(int w) {
                this.w = w;
            }

            public long getW() {
                return w;
            }

            public boolean isWithinQuery(int x1, int y1, int z1, int x2, int y2, int z2) {
                return x1 <= x && x <= x2
                        && y1 <= y && y <= y2
                        && z1 <= z && z <= z2;
            }
        }
    }
}
