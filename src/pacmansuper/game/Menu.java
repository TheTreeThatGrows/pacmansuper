package pacmansuper.game;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.audio.Music;
import com.almasb.fxgl.scene.FXGLMenu;
import com.almasb.fxgl.scene.menu.MenuType;
import com.almasb.fxgl.settings.MenuItem;
import com.almasb.fxgl.ui.FXGLButton;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.EnumSet;
import java.util.function.Supplier;

import static com.almasb.fxgl.app.FXGL.getAudioPlayer;
import static com.almasb.fxgl.app.FXGL.localizedStringProperty;

/**
 * FXGL Default Menu: This is the default code for FXGL  Menu
 * We did not write this, only tweaked it. All credits goes to its owner: Almas Baimagambetov
 *
 * @author Almas Baimagambetov
 *
 */

public class Menu extends FXGLMenu {

    //Menu Box
    public Menu(GameApplication app, MenuType type) {
        super(app, type);

        MenuBox menu = type == MenuType.MAIN_MENU
                ? createMenuBodyMainMenu()
                : createMenuBodyGameMenu();

        double menuX = 50;
        double menuY = app.getHeight() / 2 - menu.getLayoutHeight() / 2;

        menuRoot.setTranslateX(menuX);
        menuRoot.setTranslateY(menuY);

        contentRoot.setTranslateX(menuX * 2 + 200);
        contentRoot.setTranslateY(menuY);

        menuRoot.getChildren().add(menu);
        contentRoot.getChildren().add(EMPTY);

        activeProperty().addListener((observable, wasActive, isActive) -> {
            if (!isActive) {
                // the scene is no longer active so reset everything
                // so that next time scene is active everything is loaded properly
                switchMenuTo(menu);
                switchMenuContentTo(EMPTY);
            }
        });
    }

    @Override
    protected Node createBackground(double width, double height) {
        return FXGL.getAssetLoader().loadTexture("MenuBackground.png");
    }

    @Override
    protected Node createTitleView(String title) {
        Text text = FXGL.getUIFactory().newText(title, 50);

        Rectangle bg = new Rectangle(text.getLayoutBounds().getWidth() + 20, 60, null);
        bg.setStroke(Color.WHITE);
        bg.setStrokeWidth(4);

        StackPane titleRoot = new StackPane();
        titleRoot.getChildren().addAll(bg, text);

        titleRoot.setTranslateX(app.getWidth() / 2 - (text.getLayoutBounds().getWidth() + 20) / 2);
        titleRoot.setTranslateY(50);
        return titleRoot;
    }

    @Override
    protected Node createVersionView(String version) {
        Text view = FXGL.getUIFactory().newText(version);
        view.setTranslateY(app.getHeight() - 2);
        return view;
    }

    @Override
    protected Node createProfileView(String profileName) {
        Text view = FXGL.getUIFactory().newText(profileName);
        view.setTranslateY(0);
        view.setTranslateX(0);
        return view;
    }

    protected MenuBox createMenuBodyMainMenu() {
        log.debug("createMenuBodyMainMenu()");

        MenuBox box = new MenuBox(200);

        EnumSet<MenuItem> enabledItems = app.getSettings().getEnabledMenuItems();

        MenuButton itemContinue = new MenuButton("menu.continue");
        itemContinue.setOnAction(e -> fireContinue());
        box.add(itemContinue);

        itemContinue.disableProperty().bind(listener.hasSavesProperty().not());

        MenuButton itemNewGame = new MenuButton("menu.newGame");
        itemNewGame.setOnAction(e -> fireNewGame());
        box.add(itemNewGame);

        MenuButton itemLoad = new MenuButton("menu.load");
        itemLoad.setMenuContent(this::createContentLoad);
        box.add(itemLoad);

        MenuButton itemOptions = new MenuButton("menu.options");
        itemOptions.setChild(createOptionsMenu());
        box.add(itemOptions);

        MenuButton itemLogout = new MenuButton("menu.logout");
        itemLogout.setOnAction(e -> fireLogout());
        box.add(itemLogout);

        MenuButton itemExit = new MenuButton("menu.exit");
        itemExit.setOnAction(e -> fireExit());
        box.add(itemExit);

        return box;
    }

    protected MenuBox createMenuBodyGameMenu() {
        log.debug("createMenuBodyGameMenu()");

        MenuBox box = new MenuBox(200);

        EnumSet<MenuItem> enabledItems = app.getSettings().getEnabledMenuItems();

        MenuButton itemResume = new MenuButton("menu.resume");
        itemResume.setOnAction(e -> fireResume());
        box.add(itemResume);


        MenuButton itemSave = new MenuButton("menu.save");
        itemSave.setOnAction(e -> fireSave());

        MenuButton itemLoad = new MenuButton("menu.load");
        itemLoad.setMenuContent(this::createContentLoad);

        box.add(itemSave);
        box.add(itemLoad);

        MenuButton itemOptions = new MenuButton("menu.options");
        itemOptions.setChild(createOptionsMenu());
        box.add(itemOptions);


        MenuButton itemExit = new MenuButton("menu.mainMenu");
        itemExit.setOnAction(e -> fireExitToMainMenu());
        box.add(itemExit);

        return box;
    }

    protected MenuBox createOptionsMenu() {
        log.debug("createOptionsMenu()");

        MenuButton itemAudio = new MenuButton("menu.audio");
        itemAudio.setMenuContent(this::createContentAudio);

        MenuButton btnRestore = new MenuButton("menu.restore");
        btnRestore.setOnAction(e -> {
            app.getDisplay().showConfirmationBox("Settings will be restored to default", yes -> {
                if (yes) {
                    switchMenuContentTo(EMPTY);
                    listener.restoreDefaultSettings();
                }
            });
        });

        return new MenuBox(200, itemAudio, btnRestore);
    }

    @Override
    protected void switchMenuTo(Node menu) {
        Node oldMenu = menuRoot.getChildren().get(0);

        FadeTransition ft = new FadeTransition(Duration.seconds(0.33), oldMenu);
        ft.setToValue(0);
        ft.setOnFinished(e -> {
            menu.setOpacity(0);
            menuRoot.getChildren().set(0, menu);
            oldMenu.setOpacity(1);

            FadeTransition ft2 = new FadeTransition(Duration.seconds(0.33), menu);
            ft2.setToValue(1);
            ft2.play();
        });
        ft.play();
    }

    @Override
    protected void switchMenuContentTo(Node content) {
        contentRoot.getChildren().set(0, content);
    }

    private static class MenuBox extends VBox {
        private int width;

        MenuBox(int width, MenuButton... items) {
            this.width = width;

            getChildren().add(createSeparator(width));

            for (MenuButton item : items) {
                add(item);
            }
        }

        void add(MenuButton item) {
            item.setParent(this);
            getChildren().addAll(item, createSeparator(width));
        }

        private Line createSeparator(int width) {
            Line sep = new Line();
            sep.setEndX(width);
            sep.setStroke(Color.DARKGREY);
            return sep;
        }

        double getLayoutHeight() {
            return 10 * getChildren().size();
        }
    }

    private class MenuButton extends FXGLButton {
        private MenuBox parent;
        private MenuContent cachedContent = null;

        MenuButton(String stringKey) {
            textProperty().bind(localizedStringProperty(stringKey));
        }

        public void setParent(MenuBox menu) {
            parent = menu;
        }

        public void setMenuContent(Supplier<MenuContent> contentSupplier) {

            this.addEventHandler(ActionEvent.ACTION, event -> {
                if (cachedContent == null)
                    cachedContent = contentSupplier.get();

                switchMenuContentTo(cachedContent);
            });
        }

        public void setChild(MenuBox menu) {
            MenuButton back = new MenuButton("menu.back");
            menu.getChildren().add(0, back);

            back.addEventHandler(ActionEvent.ACTION, event -> switchMenuTo(MenuButton.this.parent));

            this.addEventHandler(ActionEvent.ACTION, event -> switchMenuTo(menu));
        }
    }

    @Override
    protected Button createActionButton(String name, Runnable action) {
        MenuButton btn = new MenuButton(name);
        btn.addEventHandler(ActionEvent.ACTION, event -> action.run());

        return btn;
    }
}