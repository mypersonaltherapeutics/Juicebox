/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2011-2016 Broad Institute, Aiden Lab
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

package juicebox.gui;

import juicebox.MainWindow;
import org.fest.swing.core.BasicRobot;
import org.fest.swing.core.Robot;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by mikeehman on 6/14/16.
 */
public class MainViewPanelTest {

    private final static String testURL = "https://hicfiles.s3.amazonaws.com/hiseq/hela/in-situ/combined.hic";
    private static MainWindow mainWindow;
    private static SuperAdapter superAdapter;

    /**
     * Open the application ready to be tested
     */
    @Before
    public void setup() {
        try {
            // start the GUI application
            MainWindow.main(new String[1]);

            mainWindow = (MainWindow) Window.getWindows()[0];
            superAdapter = mainWindow.getSuperAdapter();
            // need to wait for the runnable to finish
            superAdapter.safeLoad(Arrays.asList(testURL), false, "test");


        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Uses a robot to terminate the application
     *
     * @throws AWTException
     */
    @After
    public void cleanup() throws AWTException {
        Robot robot = BasicRobot.robotWithNewAwtHierarchy();
        robot.cleanUp();
    }

    /**
     * Test whether initial load of a .hic file gives All by All view
     *
     * @throws AWTException
     */
    @Test
    public void isWholeGenomeTest() throws AWTException, InterruptedException {

        Robot robot = BasicRobot.robotWithNewAwtHierarchy();

        // just being paranoid
        assertTrue(robot.isActive());
        robot.focus(mainWindow);
        assertNotNull(mainWindow.getHiC());

        assertEquals("All", superAdapter.getMainViewPanel().getChrBox1().getName());
        assertEquals("All", superAdapter.getMainViewPanel().getChrBox2().getName());
//        assertEquals("Initial view is not All by All", "All", tmp.getText());
        robot.cleanUpWithoutDisposingWindows();
    }

}