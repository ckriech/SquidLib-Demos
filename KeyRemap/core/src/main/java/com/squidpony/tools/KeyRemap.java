package com.squidpony.tools;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import squidpony.squidgrid.gui.gdx.*;
import squidpony.squidmath.Coord;
import squidpony.squidmath.OrderedMap;

import static squidpony.squidgrid.gui.gdx.SquidInput.*;

/**
 * This is a small, not-overly-simple demo that presents some important features of SquidLib and shows a faster,
 * cleaner, and more recently-introduced way of displaying the map and other text. Features include dungeon map
 * generation, field of view, pathfinding (to the mouse position), continuous noise (used for a wavering torch effect),
 * language generation/ciphering, a colorful glow effect, and ever-present random number generation (with a seed).
 * You can increase the size of the map on most target platforms (but GWT struggles with large... anything) by
 * changing gridHeight and gridWidth to affect the visible area or bigWidth and bigHeight to adjust the size of the
 * dungeon you can move through, with the camera following your '@' symbol.
 * <br>
 * The assets folder of this project, if it was created with SquidSetup, will contain the necessary font files (just one
 * .fnt file and one .png are needed, but many more are included by default). You should move any font files you don't
 * use out of the assets directory when you produce a release JAR, APK, or GWT build.
 */
public class KeyRemap extends ApplicationAdapter {
    SpriteBatch batch;

    private SparseLayers display;
    private char[][] contents;
    private float[][] colors, bgColors;

    //Here, gridHeight refers to the total number of rows to be displayed on the screen.
    //We're displaying 25 rows of dungeon, then 7 more rows of text generation to show some tricks with language.
    //gridHeight is 25 because that variable will be used for generating the dungeon (the actual size of the dungeon
    //will be triple gridWidth and triple gridHeight), and determines how much off the dungeon is visible at any time.
    //The bonusHeight is the number of additional rows that aren't handled like the dungeon rows and are shown in a
    //separate area; here we use them for translations. The gridWidth is 90, which means we show 90 grid spaces
    //across the whole screen, but the actual dungeon is larger. The cellWidth and cellHeight are 10 and 20, which will
    //match the starting dimensions of a cell in pixels, but won't be stuck at that value because we use a "Stretchable"
    //font, and so the cells can change size (they don't need to be scaled by equal amounts, either). While gridWidth
    //and gridHeight are measured in spaces on the grid, cellWidth and cellHeight are the initial pixel dimensions of
    //one cell; resizing the window can make the units cellWidth and cellHeight use smaller or larger than a pixel.

    /** In number of cells */
    private static final int gridWidth = 160;
    /** In number of cells */
    private static final int gridHeight = 35;

    /** In number of cells */
    private static final int bigWidth = gridWidth;
    /** In number of cells */
    private static final int bigHeight = gridHeight;

    /** In number of cells */
    private static final int bonusHeight = 0;
    /** The pixel width of a cell */
    private static final int cellWidth = 8;
    /** The pixel height of a cell */
    private static final int cellHeight = 17;
    private SquidInput input;
    private Color bgColor;
    private Stage stage;
    private Coord selectedKey;
    private boolean shifted, ctrled, alted;

    @Override
    public void create () {
        String[] shiftKeyNames = {
                "A",
                "B",
                "C",
                "D",
                "E",
                "F",
                "G",
                "H",
                "I",
                "J",
                "K",
                "L",
                "M",
                "N",
                "O",
                "P",
                "Q",
                "R",
                "S",
                "T",
                "U",
                "V",
                "W",
                "X",
                "Y",
                "Z",
                "Space",
                "<",
                ">",
                "~",
                "_",
                "+",
                "{",
                "}",
                "|",
                ":",
                "\"",
                "?",
                ")",
                "!",
                "@",
                "#",
                "$",
                "%",
                "^",
                "&",
                "*",
                "(",
                "Vertical Arrow",
                "Down-Left Arrow",
                "Down Arrow",
                "Down-Right Arrow",
                "Left Arrow",
                "Center Arrow",
                "Right Arrow",
                "Up-Left Arrow",
                "Up Arrow",
                "Up-Right Arrow",
                "Enter",
                "Tab",
                "Home",
                "End",
                "Backspace",
                "Forward Delete",
                "Escape",
                "Page Up",
                "Page Down",
                "Dedicated :",
                "Dedicated *",
                "Dedicated #",
                "Dedicated @",
                "Gamepad A",
                "Gamepad B",
                "Gamepad C",
                "Gamepad X",
                "Gamepad Y",
                "Gamepad Z",
                "Gamepad L1",
                "Gamepad R1",
                "Gamepad L2",
                "Gamepad R2",
                "Gamepad Left Thumb",
                "Gamepad Right Thumb",
                "Gamepad Start",
                "Gamepad Select",
                "F1",
                "F2",
                "F3",
                "F4",
                "F5",
                "F6",
                "F7",
                "F8",
                "F9",
                "F10",
                "F11",
                "F12",
                "Insert",
        }, keyNames = {
                "a",
                "b",
                "c",
                "d",
                "e",
                "f",
                "g",
                "h",
                "i",
                "j",
                "k",
                "l",
                "m",
                "n",
                "o",
                "p",
                "q",
                "r",
                "s",
                "t",
                "u",
                "v",
                "w",
                "x",
                "y",
                "z",
                "Space",
                ",",
                ".",
                "`",
                "-",
                "=",
                "[",
                "]",
                "'",
                ";",
                "\\",
                "/",
                "0",
                "1",
                "2",
                "3",
                "4",
                "5",
                "6",
                "7",
                "8",
                "9",
                "Vertical Arrow",
                "Down-Left Arrow",
                "Down Arrow",
                "Down-Right Arrow",
                "Left Arrow",
                "Center Arrow",
                "Right Arrow",
                "Up-Left Arrow",
                "Up Arrow",
                "Up-Right Arrow",
                "Enter",
                "Tab",
                "Home",
                "End",
                "Backspace",
                "Forward Delete",
                "Escape",
                "Page Up",
                "Page Down",
                "Dedicated :",
                "Dedicated *",
                "Dedicated #",
                "Dedicated @",
                "Gamepad A",
                "Gamepad B",
                "Gamepad C",
                "Gamepad X",
                "Gamepad Y",
                "Gamepad Z",
                "Gamepad L1",
                "Gamepad R1",
                "Gamepad L2",
                "Gamepad R2",
                "Gamepad Left Thumb",
                "Gamepad Right Thumb",
                "Gamepad Start",
                "Gamepad Select",
                "F1",
                "F2",
                "F3",
                "F4",
                "F5",
                "F6",
                "F7",
                "F8",
                "F9",
                "F10",
                "F11",
                "F12",
                "Insert",
        };
        Integer[] shiftKeys = {
                0x80000|'A',
                0x80000|'B',
                0x80000|'C',
                0x80000|'D',
                0x80000|'E',
                0x80000|'F',
                0x80000|'G',
                0x80000|'H',
                0x80000|'I',
                0x80000|'J',
                0x80000|'K',
                0x80000|'L',
                0x80000|'M',
                0x80000|'N',
                0x80000|'O',
                0x80000|'P',
                0x80000|'Q',
                0x80000|'R',
                0x80000|'S',
                0x80000|'T',
                0x80000|'U',
                0x80000|'V',
                0x80000|'W',
                0x80000|'X',
                0x80000|'Y',
                0x80000|'Z',
                0x80000|' ',
                0x80000|'<',
                0x80000|'>',
                0x80000|'~',
                0x80000|'_',
                0x80000|'+',
                0x80000|'{',
                0x80000|'}',
                0x80000|'|',
                0x80000|':',
                0x80000|'"',
                0x80000|'?',
                0x80000|')',
                0x80000|'!',
                0x80000|'@',
                0x80000|'#',
                0x80000|'$',
                0x80000|'%',
                0x80000|'^',
                0x80000|'&',
                0x80000|'*',
                0x80000|'(',
                0x80000|VERTICAL_ARROW,
                0x80000|DOWN_LEFT_ARROW,
                0x80000|DOWN_ARROW,
                0x80000|DOWN_RIGHT_ARROW,
                0x80000|LEFT_ARROW,
                0x80000|CENTER_ARROW,
                0x80000|RIGHT_ARROW,
                0x80000|UP_LEFT_ARROW,
                0x80000|UP_ARROW,
                0x80000|UP_RIGHT_ARROW,
                0x80000|ENTER,
                0x80000|TAB,
                0x80000|HOME,
                0x80000|END,
                0x80000|BACKSPACE,
                0x80000|FORWARD_DELETE,
                0x80000|ESCAPE,
                0x80000|PAGE_UP,
                0x80000|PAGE_DOWN,
                0x80000|':',
                0x80000|'*',
                0x80000|'#',
                0x80000|'@',
                0x80000|GAMEPAD_A,
                0x80000|GAMEPAD_B,
                0x80000|GAMEPAD_C,
                0x80000|GAMEPAD_X,
                0x80000|GAMEPAD_Y,
                0x80000|GAMEPAD_Z,
                0x80000|GAMEPAD_L1,
                0x80000|GAMEPAD_R1,
                0x80000|GAMEPAD_L2,
                0x80000|GAMEPAD_R2,
                0x80000|GAMEPAD_LEFT_THUMB,
                0x80000|GAMEPAD_RIGHT_THUMB,
                0x80000|GAMEPAD_START,
                0x80000|GAMEPAD_SELECT,
                0x80000|F1,
                0x80000|F2,
                0x80000|F3,
                0x80000|F4,
                0x80000|F5,
                0x80000|F6,
                0x80000|F7,
                0x80000|F8,
                0x80000|F9,
                0x80000|F10,
                0x80000|F11,
                0x80000|F12,
                0x80000|INSERT,
        }, keys = {
                0|'a',
                0|'b',
                0|'c',
                0|'d',
                0|'e',
                0|'f',
                0|'g',
                0|'h',
                0|'i',
                0|'j',
                0|'k',
                0|'l',
                0|'m',
                0|'n',
                0|'o',
                0|'p',
                0|'q',
                0|'r',
                0|'s',
                0|'t',
                0|'u',
                0|'v',
                0|'w',
                0|'x',
                0|'y',
                0|'z',
                0|' ',
                0|',',
                0|'.',
                0|'`',
                0|'-',
                0|'=',
                0|'[',
                0|']',
                0|'\\',
                0|',',
                0|'\'',
                0|'/',
                0|'0',
                0|'1',
                0|'2',
                0|'3',
                0|'4',
                0|'5',
                0|'6',
                0|'7',
                0|'8',
                0|'9',
                0|VERTICAL_ARROW,
                0|DOWN_LEFT_ARROW,
                0|DOWN_ARROW,
                0|DOWN_RIGHT_ARROW,
                0|LEFT_ARROW,
                0|CENTER_ARROW,
                0|RIGHT_ARROW,
                0|UP_LEFT_ARROW,
                0|UP_ARROW,
                0|UP_RIGHT_ARROW,
                0|ENTER,
                0|TAB,
                0|HOME,
                0|END,
                0|BACKSPACE,
                0|FORWARD_DELETE,
                0|ESCAPE,
                0|PAGE_UP,
                0|PAGE_DOWN,
                0|':',
                0|'*',
                0|'#',
                0|'@',
                0|GAMEPAD_A,
                0|GAMEPAD_B,
                0|GAMEPAD_C,
                0|GAMEPAD_X,
                0|GAMEPAD_Y,
                0|GAMEPAD_Z,
                0|GAMEPAD_L1,
                0|GAMEPAD_R1,
                0|GAMEPAD_L2,
                0|GAMEPAD_R2,
                0|GAMEPAD_LEFT_THUMB,
                0|GAMEPAD_RIGHT_THUMB,
                0|GAMEPAD_START,
                0|GAMEPAD_SELECT,
                0|F1,
                0|F2,
                0|F3,
                0|F4,
                0|F5,
                0|F6,
                0|F7,
                0|F8,
                0|F9,
                0|F10,
                0|F11,
                0|F12,
                0|INSERT,
        };
        
        OrderedMap<String, Integer> keyNameMap = new OrderedMap<>(keyNames, keys),
                shiftKeyNameMap = new OrderedMap<>(shiftKeyNames, shiftKeys); 

        //Some classes in SquidLib need access to a batch to render certain things, so it's a good idea to have one.
        batch = new SpriteBatch();
        StretchViewport mainViewport = new StretchViewport(gridWidth * cellWidth, gridHeight * cellHeight);
        mainViewport.setScreenBounds(0, 0, gridWidth * cellWidth, gridHeight * cellHeight);
        //Here we make sure our Stage, which holds any text-based grids we make, uses our Batch.
        stage = new Stage(mainViewport, batch);
        display = new SparseLayers(bigWidth, bigHeight, cellWidth, cellHeight,
                DefaultResources.getCrispSlabFont());

        // A bit of a hack to increase the text height slightly without changing the size of the cells they're in.
        // This causes a tiny bit of overlap between cells, which gets rid of an annoying gap between solid lines.
        // If you use '#' for walls instead of box drawing chars, you don't need this.
        // If you don't use DefaultResources.getStretchableSlabFont(), you may need to adjust the multipliers here.
        display.font.tweakWidth(cellWidth * 1.125f).tweakHeight(cellHeight * 1.1f).initBySize();

        //The next line sets the background color for anything we don't draw on.
        bgColor = SColor.CW_PALE_BROWN;

        contents = new char[gridWidth][gridHeight];
        colors = new float[gridWidth][gridHeight];
        bgColors = new float[gridWidth][gridHeight];
        String name;
        System.out.println(keyNames.length);
        System.out.println(keyNameMap.size());
        System.out.println();
        for (int x = 1, idx = 0; x < gridWidth - 50 && idx < keys.length; x+=54) { 
            for (int y = 0; y < gridHeight - 1 && idx < keys.length; y++) { 
                name = shifted ? shiftKeyNameMap.keyAt(idx) : keyNameMap.keyAt(idx);
                for (int i = 5, p = 0; i < 25 && p < name.length(); i++, p++) {
                    contents[x+i][y] = name.charAt(p);
                    colors[x+i][y] = -0x1.684044p125F;//SColor.DB_INK
                    bgColors[x+i][y] = -0x1.b9ebeap126F;//SColor.BEIGE
                }
                for (int i = 5 + name.length(); i < 25; i++) {
                    contents[x+i][y] = ' ';
                    colors[x+i][y] = 0F;
                    bgColors[x+i][y] = -0x1.b9ebeap126F;//SColor.BEIGE
                }
                bgColors[x][y] = -0x1.b9ebeap126F;//SColor.BEIGE

                bgColors[x+1][y] = -0x1.a4acb2p125F;//SColor.DB_SOOT
                bgColors[x+2][y] = -0x1.a4acb2p125F;//SColor.DB_SOOT
                bgColors[x+3][y] = -0x1.a4acb2p125F;//SColor.DB_SOOT

                bgColors[x+4][y] = -0x1.b9ebeap126F;//SColor.BEIGE

                if(ctrled)
                {
                    contents[x+1][y] = 'C';
                    colors[x+1][y] = -0x1.d9e268p126F;//SColor.CW_BRIGHT_CYAN
                }
                if(alted)
                {
                    contents[x+2][y] = 'A';
                    colors[x+2][y] = -0x1.d913b4p126F;//SColor.CW_LIGHT_PURPLE
                }
                if(shifted)
                {
                    contents[x+3][y] = 'S';
                    colors[x+3][y] = -0x1.7677e8p125F;//SColor.CW_BRIGHT_RED
                }
                idx++;
            }
        }
        selectedKey = Coord.get(0,0);

        //These need to have their positions set before adding any entities if there is an offset involved.
        //There is no offset used here, but it's still a good practice here to set positions early on.
        display.setPosition(0f, 0f);
        
        // this is a big one.
        // SquidInput can be constructed with a KeyHandler (which just processes specific keypresses), a SquidMouse
        // (which is given an InputProcessor implementation and can handle multiple kinds of mouse move), or both.
        // keyHandler is meant to be able to handle complex, modified key input, typically for games that distinguish
        // between, say, 'q' and 'Q' for 'quaff' and 'Quip' or whatever obtuse combination you choose. The
        // implementation here handles hjkl keys (also called vi-keys), numpad, arrow keys, and wasd for 4-way movement.
        // Shifted letter keys produce capitalized chars when passed to KeyHandler.handle(), but we don't care about
        // that so we just use two case statements with the same body, i.e. one for 'A' and one for 'a'.
        // You can also set up a series of future moves by clicking within FOV range, using mouseMoved to determine the
        // path to the mouse position with a DijkstraMap (called playerToCursor), and using touchUp to actually trigger
        // the event when someone clicks.
        input = new SquidInput(new SquidInput.KeyHandler() {
            @Override
            public void handle(char key, boolean alt, boolean ctrl, boolean shift) {
            }
        },
                //The second parameter passed to a SquidInput can be a SquidMouse, which takes mouse or touchscreen
                //input and converts it to grid coordinates (here, a cell is 10 wide and 20 tall, so clicking at the
                // pixel position 16,51 will pass screenX as 1 (since if you divide 16 by 10 and round down you get 1),
                // and screenY as 2 (since 51 divided by 20 rounded down is 2)).
                new SquidMouse(cellWidth, cellHeight, gridWidth, gridHeight, 0, 0, new InputAdapter() {

            // if the user clicks and mouseMoved hasn't already assigned a path to toCursor, then we call mouseMoved
            // ourselves and copy toCursor over to awaitedMoves.
            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                mouseMoved(screenX, screenY);
                return true;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                return mouseMoved(screenX, screenY);
            }

            // causes the path to the mouse position to become highlighted (toCursor contains a list of Coords that
            // receive highlighting). Uses DijkstraMap.findPathPreScanned() to find the path, which is rather fast.
            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                // we also need to check if screenX or screenY is out of bounds.
                if(screenX < 0 || screenY < 0 || screenX >= gridWidth || screenY >= gridHeight ||
                        (selectedKey.x == screenX && selectedKey.y == screenY) || contents[screenX][screenY] == 0)
                {
                    return false;
                }
                selectedKey = Coord.get(screenX, screenY);
                return false;
            }
        }));
        //Setting the InputProcessor is ABSOLUTELY NEEDED TO HANDLE INPUT
        Gdx.input.setInputProcessor(new InputMultiplexer(stage, input));
        //we add display, our one visual component that moves, to the list of things that act in the main Stage.
        stage.addActor(display);


    }

    /**
     * Draws the map, applies any highlighting for the path to the cursor, and then draws the player.
     */
    public void putMap()
    {
        //In many other situations, you would clear the drawn characters to prevent things that had been drawn in the
        //past from affecting the current frame. This isn't a problem here, but would probably be an issue if we had
        //monsters running in and out of our vision. If artifacts from previous frames show up, uncomment the next line.
        //display.clear();
        
        display.put(contents, colors, bgColors); 
        display.putWithLight(selectedKey.x, selectedKey.y, bgColors[selectedKey.x][selectedKey.y], SColor.FLOAT_BLACK, 0.25f);
    }
    @Override
    public void render () {
        // standard clear the background routine for libGDX
        Gdx.gl.glClearColor(bgColor.r, bgColor.g, bgColor.b, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // need to display the map every frame, since we clear the screen to avoid artifacts.
        putMap();
        // if the user clicked, we have a list of moves to perform.
        // if we are waiting for the player's input and get input, process it.
        if(input.hasNext()) {
            input.next();
        }
        // certain classes that use scene2d.ui widgets need to be told to act() to process input.
        stage.act();
        // we have the main stage set itself up after the language stage has already drawn.
        stage.getViewport().apply(false);
        // stage has its own batch and must be explicitly told to draw().
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        // message box won't respond to clicks on the far right if the stage hasn't been updated with a larger size
        float currentZoomX = (float)width / gridWidth;
        // total new screen height in pixels divided by total number of rows on the screen
        float currentZoomY = (float)height / (gridHeight + bonusHeight);
        // SquidMouse turns screen positions to cell positions, and needs to be told that cell sizes have changed
        // a quirk of how the camera works requires the mouse to be offset by half a cell if the width or height is odd
        // (gridWidth & 1) is 1 if gridWidth is odd or 0 if it is even; it's good to know and faster than using % , plus
        // in some other cases it has useful traits (x % 2 can be 0, 1, or -1 depending on whether x is negative, while
        // x & 1 will always be 0 or 1).
        input.getMouse().reinitialize(currentZoomX, currentZoomY, gridWidth, gridHeight,
                (gridWidth & 1) * (int)(currentZoomX * -0.5f), (gridHeight & 1) * (int) (currentZoomY * -0.5f));        // the viewports are updated separately so each doesn't interfere with the other's drawn area.
        stage.getViewport().update(width, height, false);
    }
}
// An explanation of hexadecimal float/double literals was mentioned earlier, so here it is.
// The literal 0x1p-9f is a good example; it is essentially the same as writing 0.001953125f,
// (float)Math.pow(2.0, -9.0), or (1f / 512f), but is possibly faster than the last two if the
// compiler can't optimize float division effectively, and is a good tool to have because these
// hexadecimal float or double literals always represent numbers accurately. To contrast,
// 0.3 - 0.2 is not equal to 0.1 with doubles, because tenths are inaccurate with floats and
// doubles, and hex literals won't have the option to write an inaccurate float or double.
// There's some slightly confusing syntax used to write these literals; the 0x means the first
// part uses hex digits (0123456789ABCDEF), but the p is not a hex digit and is used to start
// the "p is for power" exponent section. In the example, I used -9 for the power; this is a
// base 10 number, and is used to mean a power of 2 that the hex digits will be multiplied by.
// Because the -9 is a base 10 number, the f at the end is not a hex digit, and actually just
// means the literal is a float, in the same way 1.5f is a float. 2.0 to the -9 is the same as
// 1.0 / Math.pow(2.0, 9.0), but re-calculating Math.pow() is considerably slower if you run it
// for every cell during every frame. Though this is most useful for negative exponents because
// there are a lot of numbers after the decimal point to write out with 0.001953125 or the like,
// it is also sometimes handy when you have an integer or long written in hexadecimal and want
// to make it a float or double. You could use the hex long 0x9E3779B9L, for instance, but to
// write that as a double you would use 0x9E3779B9p0 , not the invalid syntax 0x9E3779B9.0 .
// We use p0 there because 2 to the 0 is 1, so multiplying by 1 gets us the same hex number.
// Very large numbers can also benefit by using a large positive exponent; using p10 and p+10
// as the last part of a hex literal are equivalent. You can see the hex literal for any given
// float with Float.toHexString(float), or for a double with Double.toHexString(double) .
// SColor provides the packed float versions of all color constants as hex literals in the
// documentation for each SColor.
// More information here: https://blogs.oracle.com/darcy/hexadecimal-floating-point-literals
