/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2011-2017 Broad Institute, Aiden Lab
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */

package juicebox.tools.dev;

import java.io.*;
import java.util.*;

class HiCArtMNDWriter {

    public static void main(String[] args) {
        writeMergedNoDupsFromTimeSeq(args[0], args[1]);
    }

    private static void writeMergedNoDupsFromTimeSeq(String seqPath, String newPath) {
        List<Integer[]> listPositions = new ArrayList<>();
        int distanceLimit = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(seqPath))) {
            Set<Integer> positions = new HashSet<>();

            for (String line; (line = br.readLine()) != null; ) {
                String[] parts = line.split(",");
                Integer[] xy = new Integer[]{Integer.parseInt(parts[0]), Integer.parseInt(parts[1])};
                listPositions.add(xy);
                positions.add(xy[0]);
                positions.add(xy[1]);
            }

            distanceLimit = (Collections.max(positions) - Collections.min(positions)) / 2;
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }

        try {
            PrintWriter p0 = new PrintWriter(new FileWriter(newPath));
            for (int i = 0; i < listPositions.size(); i++) {
                Integer[] pos_xy_1 = listPositions.get(i);
                for (int j = i; j < listPositions.size(); j++) {
                    Integer[] pos_xy_2 = listPositions.get(j);
                    double distance = Math.sqrt((pos_xy_1[0] - pos_xy_2[0]) ^ 2 + (pos_xy_1[1] - pos_xy_2[1]) ^ 2);
                    if (distance < distanceLimit) {
                        double value = 1. / Math.max(.5, distance);
                        if (!Double.isNaN(value) && value > 0) {
                            p0.println("0 art " + i + " 0 16 art " + j + " 1 " + value);
                        }
                    }
                }
            }
            p0.close();
        } catch (IOException ignored) {
            ignored.printStackTrace();
        }
    }
}
