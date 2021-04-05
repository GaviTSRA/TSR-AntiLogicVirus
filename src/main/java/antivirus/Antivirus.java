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

    private static String version = "2.0";                              //Plugin version dislayed by /info
    public Tile lastVirus;                                              //a variable to store the last virus, so the chat isnt spammed to much
    public static String[] virusBlocks = {                              //a variable to store virus args
    "@micro-processor", "@logic-processor", "@hyper-processor", ""}; 
    public static String[] virusConfigs = {"@this", "processor", ""};   //a variable to store virus args

    //constructor (used for events)
    public Antivirus() {

        //triggers when a player ends a build
        Events.on(BlockBuildEndEvent.class, e -> {
            Tile tile = e.tile;                                                 //get the tile
            if(tile.build instanceof LogicBuild)                                //if it is a logic block
            {
                String code = ((LogicBuild)tile.build).code;                    //get the code
                if(code.contains("ucontrol build"))                             //if it controls a unitn
                {
                    int controlIndex = code.indexOf("ucontrol build");          //get the index of the control statement
                    String controlStatement = code.substring(controlIndex);     //get the control statement
                    String[] codeParts = controlStatement.split(" ");           //split it into the args
                    codeParts[6] = codeParts[6].split("\n")[0];                 //fix the last arg

                    for(int i = 0; i<=virusBlocks.length - 1; i++)              //loop trough the virus blocks and check if the arg matches
                    {
                        if(virusBlocks[i].contains(codeParts[4]))               //if it does
                        {
                            for(int ii = 0; ii<=virusConfigs.length - 1; ii++)  //loop through the virus configs and check if it matches
                            {
                                if (virusConfigs[ii].contains(codeParts[6]))    //if it does
                                {
                                                                                //send a message
                                    if(tile != lastVirus) Call.sendMessage("High probability of logic virus at " + tile.build.x / Vars.tilesize + ", " + tile.build.y / Vars.tilesize + ", build by " + tile.build.lastAccessed + "[white] !!! DELETING!!!");
                                    tile.setNet(Blocks.air);                    //delete the virus
                                    lastVirus = tile;                           //set the last virus
                                    return;                                     //and return
                                }    
                            }
                        }
                    }
                }
            }
        });

        //triggers when a block is edited
        Events.on(ConfigEvent.class, e -> {
            Building build = e.tile; //get the build
            if(build instanceof LogicBuild) //if it is a logic build
            {
                String code = ((LogicBuild)build).code;                         //get the code
                
                if(code.contains("ucontrol build"))                             //if it controls a unit
                {
                    int controlIndex = code.indexOf("ucontrol build");          //get the index of the control statement
                    String controlStatement = code.substring(controlIndex);     //get the control statement
                    String[] codeParts = controlStatement.split(" ");           //seperate it into its args
                    codeParts[6] = codeParts[6].split("\n")[0];                 //fix the last arg

                    for(int i = 0; i<virusBlocks.length - 1; i++)               //loop over all possibilitys for viruses in arg 4
                    {
                        if(virusBlocks[i].contains(codeParts[4]))               //if it is a virus args
                        {
                            for(int ii = 0; ii<=virusConfigs.length - 1; ii++)  //loop over the virus args for args 6
                            {
                                if (virusConfigs[ii].contains(codeParts[6]))    //if it is one
                                {
                                    //send a message
                                    if(build.tile != lastVirus) Call.sendMessage("High probability of logic virus at " + build.x / Vars.tilesize + ", " + build.y / Vars.tilesize + ", build by " + build.lastAccessed + "[white] !!! DELETING!!!");
                                    build.tile.setNet(Blocks.air);  //delete the virus
                                    lastVirus = build.tile;         //set the lastvirus
                                    return;                         //and return
                                }
                            }
                        }
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