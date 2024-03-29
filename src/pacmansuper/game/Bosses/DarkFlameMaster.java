package pacmansuper.game.Bosses;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Control;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.time.LocalTimer;
import javafx.util.Duration;

import java.util.Random;

import static com.almasb.fxgl.app.DSLKt.play;
import static com.almasb.fxgl.app.DSLKt.spawn;

/**
 * Boss Class: Dark Flame Master
 *
 * @author Earl John Laguardia
 */

public class DarkFlameMaster extends Control {

    // ------------------------------------- <<< Data >>> -------------------------------------
    private PhysicsComponent physics;
    private Entity player;


    // ------------------------------------- <<< Player Animation >>> -------------------------------------
    //Animation
    private AnimatedTexture texture;
    private LocalTimer aiTimer;
    private AnimationChannel idle;

    public DarkFlameMaster() {

    }

    //Set entity texture
    @Override
    public void onAdded(Entity entity) {
        aiTimer = FXGL.newLocalTimer();
        aiTimer.capture();
    }
    // ------------------------------------- <<< Dark Flame Master AI>>> -------------------------------------
    @Override
    public void onUpdate(Entity entity, double tpf) {

        //Dark Flame Master AI
        if(aiTimer.elapsed(Duration.seconds(0.7))) {
            Random rand = new Random();
            int  rnd = rand.nextInt(10) + 0;
            aiTimer.capture();

            if(rnd <= 5) {
                enemyRight();
            } else {
                enemyLeft();
            }
        }
    }


    // ------------------------------------- <<< Movement >>> -------------------------------------

    /**
     * Method for Moving Right
     *
     * @author Earl John Laguardia
     */


    public void enemyRight() {

        getEntity().setScaleX(-1); //Enemy faces right
        physics.setVelocityX(700); //Move right Velocity
    }


    /**
     * Method for Moving Left
     *
     * @author Earl John Laguardia
     */

    public void enemyLeft() {

        getEntity().setScaleX(1); //Enemy faces left
        physics.setVelocityX(-700); //Move left Velocity
    }
}


