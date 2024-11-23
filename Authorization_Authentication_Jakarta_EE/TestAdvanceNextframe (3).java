/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import cst8218.hasib.entity.Bouncer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author noah8
 */
public class TestAdvanceNextframe {
    
    public TestAdvanceNextframe() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

     @Test
     public void testVelocity() {
         Bouncer bouncer = new Bouncer();
         bouncer.setX(10);
         bouncer.setY(10);
         bouncer.setyVelocity(15);
         
         int answer = 26;
         
         bouncer.advanceOneFrame();
         
         int newAns = bouncer.getY();
         
         assertEquals(answer, newAns);
     } @Test
     public void testNegativeVelocity() {
         Bouncer bouncer = new Bouncer();
         bouncer.setX(10);
         bouncer.setY(10);
         bouncer.setyVelocity(-3);
         
         int answer = 8;
         
         bouncer.advanceOneFrame();
         
         int newAns = bouncer.getY();
         
         assertEquals(answer, newAns);
     }
     public void testNegativeY() {
         Bouncer bouncer = new Bouncer();
         bouncer.setX(10);
         bouncer.setY(-10);
         bouncer.setyVelocity(15);
         
         int answer = -10;
         
         bouncer.advanceOneFrame();
         
         int newAns = bouncer.getY();
         
         assertEquals(answer, newAns);
     }
     
     @Test
     public void testYMax(){
         Bouncer bouncer = new Bouncer();
         bouncer.setX(10);
         bouncer.setY(599);
         bouncer.setyVelocity(15);
            
         int answer = 615;
         
         bouncer.advanceOneFrame();
         
         int newAns = bouncer.getY();
         
         assertEquals(answer, newAns);     
     }
     
     @Test
     public void testYMin(){
         Bouncer bouncer = new Bouncer();
         bouncer.setX(10);
         bouncer.setY(1);
         bouncer.setyVelocity(15);
         
         int answer = 17;
         
         bouncer.advanceOneFrame();
         
         int newAns = bouncer.getY();
         
         assertEquals(answer, newAns);   
     }
}
