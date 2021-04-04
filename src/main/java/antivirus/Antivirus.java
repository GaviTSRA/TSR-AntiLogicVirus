package antivirus;

import arc.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.game.Team;
import mindustry.mod.Plugin;
import mindustry.game.EventType.*;
import mindustry.net.Administration.*;
import mindustry.content.*;
import mindustry.world.*;
import mindustry.world.blocks.logic.LogicBlock.LogicBuild;
import java.util.HashSet;
import static mindustry.Vars.*;

public class Antivirus extends Plugin {

    private static String version = "1.0";  //Plugin version dislayed by /info
    public Tile lastVirus;                  //a variable to store the last virus, so the chat isnt spammed to much

    //constructor (used for events)
    public Antivirus() {

        //triggers when a player ends a build
        Events.on(BlockBuildEndEvent.class, e -> {
            Tile tile = e.tile;                                                  //get the tile
            if(tile.build instanceof LogicBuild) {                               //if it is a logic block
                String code = ((LogicBuild)tile.build).code;                     //get the code
                if(code.contains("ucontrol build") && code.contains("@this")) {  //if it controls unit and uses @this
                    
                    //check if it uses @*-processors
                    if(code.contains("@micro-processor") || code.contains("@logic-processor") || code.contains("@hyper-processor")) {
                        
                        //if this is true, send a message if it isnt the last virus
                        if(tile != lastVirus) Call.sendMessage("High probability of logic virus at " + tile.worldx() + ", " + tile.worldy() + ", build by " + tile.build.lastAccessed + "[white] !!! DELETING!!!");
                        
                        //delete the virus
                        tile.setNet(Blocks.air);

                        //and set the last virus
                        lastVirus = tile;  
                    }
                }
            }
        });

        //triggers when a block is edited
        Events.on(ConfigEvent.class, e -> {
            Building build = e.tile;                                              //get the block
            if(build instanceof LogicBuild) {                                     //if it is a logic block
                String code = ((LogicBuild)build).code;                           //get the code
                if(code.contains("ucontrol build") && code.contains("@this")) {   //if it controls units and uses @this

                    //do the virus check
                    if(code.contains("@this") && (code.contains("@micro-processor") || code.contains("@logic-processor") || code.contains("@hyper-processor"))) {
                        
                        //if its true, send a message
                        Call.sendMessage("High probability of logic virus at " + build.x + ", " + build.y + ", build by " + build.lastAccessed + "[white] !!! DELETING!!!");
                        
                        //and delete the virus
                        build.tile.setNet(Blocks.air); 
                    }
                }
            }
        });
    }

    @Override //commands that players can use ingame
    public void registerClientCommands(CommandHandler handler) {

        //send the player some info about the plugin
        handler.<Player>register("info", "", "Prints some info to the chat", (args, player) -> {
            player.sendMessage("TSR Anti Logic Virus Plugin v" + version); //send the plugin version
        });
    }
}