package minechem.handler;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import minechem.Compendium;
import minechem.Config;
import minechem.helper.ColourHelper;
import minechem.helper.FileHelper;
import minechem.helper.LogHelper;
import minechem.item.journal.pages.EntryPage;
import minechem.item.journal.pages.elements.IJournalElement;
import minechem.item.journal.pages.elements.JournalHeader;
import minechem.item.journal.pages.elements.JournalImage;
import minechem.item.journal.pages.elements.JournalText;
import minechem.registry.JournalRegistry;
import minechem.registry.MoleculeRegistry;
import net.afterlifelochie.fontbox.document.property.AlignmentMode;
import net.afterlifelochie.fontbox.document.property.FloatMode;
import org.apache.logging.log4j.Level;

public class MoleculeHandler
{
    static String moleculeChapter = "chemicals.compounds";

    public static void init()
    {
        String[] fileDestSource = new String[2];
        fileDestSource[0] = Compendium.Config.dataJsonPrefix + Compendium.Config.moleculesDataJson;
        fileDestSource[1] = Compendium.Config.configPrefix + Compendium.Config.dataJsonPrefix + Compendium.Config.moleculesDataJson;
        InputStream inputStream = FileHelper.getJsonFile(MoleculeHandler.class, fileDestSource, Config.useDefaultMolecules);
        readFromStream(inputStream);
        if (inputStream != null)
        {
            try
            {
                inputStream.close();
            } catch (IOException e)
            {
                LogHelper.exception("Cannot close stream!", e, Level.WARN);
            }
        }
    }

    private static void readFromStream(InputStream stream)
    {
        JsonReader jReader = new JsonReader(new InputStreamReader(stream));
        JsonParser parser = new JsonParser();

        JsonObject object = parser.parse(jReader).getAsJsonObject();

        readFromObject(object.entrySet(), 0);

        //saveJson(object);

        LogHelper.info("Total of " + MoleculeRegistry.getInstance().getMolecules().size() + " moleculeChapter registered");
    }

    public static void saveJson(JsonObject object)
    {
        Gson gson = new Gson();
        String json = gson.toJson(object);
        json = json.replaceAll("\\{","{\n\t\t").replaceAll(",", ",\n\t\t").replaceAll("}","\n\t}");
        try
        {
            FileWriter writer = new FileWriter("C:\\Users\\Charlie\\Documents\\Modding\\Minechem\\Minechem\\src\\main\\resources\\assets\\minechem\\data\\output.json");
            writer.write(json);
            writer.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void readFromObject(Set<Map.Entry<String, JsonElement>> elementSet, int run)
    {
        if (elementSet.isEmpty()) return;
        else if (run>4)
        {
            for (Map.Entry<String, JsonElement> moleculeEntry : elementSet)
            {
                LogHelper.warn("Molecule Parsing Error: "+moleculeEntry.getKey()+" cannot be parsed.");
            }
        }
        else
        {
            Map<String, JsonElement> unparsed = new HashMap<String, JsonElement>();
            for (Map.Entry<String, JsonElement> moleculeEntry : elementSet)
            {
                if (!moleculeEntry.getValue().isJsonObject())
                {
                    continue;
                }
                JsonObject elementObject = moleculeEntry.getValue().getAsJsonObject();
                String form = "liquid";
                if (elementObject.has("MeltingPt") && elementObject.get("MeltingPt").getAsDouble() > 25) form = "solid";
                else if (elementObject.has("BoilingPt") && elementObject.get("BoilingPt").getAsDouble() < 25) form = "gas";
                int colour = 0;
                if (elementObject.has("Colour") && elementObject.get("Colour").isJsonPrimitive())
                {
                    JsonPrimitive cInput = elementObject.getAsJsonPrimitive("Colour");
                    if (cInput.isString()) colour = ColourHelper.RGB(cInput.getAsString());
                    else if (cInput.isNumber()) colour = cInput.getAsInt();
                }
                if (MoleculeRegistry.getInstance().registerMolecule(moleculeEntry.getKey(), form, colour, elementObject.get("Formula").getAsString()))
                {
                    unparsed.put(moleculeEntry.getKey(), moleculeEntry.getValue());
                } else
                {
//                    if (elementObject.has("SMILES") && !elementObject.has("Height"))
//                    {
//                        int[] result = MoleculeImageParser.parser(moleculeEntry.getKey(), elementObject.get("SMILES").getAsString());
//                        if (result!=null)
//                        {
//                            elementObject.add("Height",new JsonPrimitive(result[0]));
//                            elementObject.add("Width",new JsonPrimitive(result[1]));
//                        }
//                    }
                    ArrayList<IJournalElement> elements = new ArrayList<IJournalElement>();
                    String pictureName = moleculeEntry.getKey().toLowerCase().replaceAll("\\s","_");
                    String pageKey = moleculeChapter + "." + pictureName;
                    elements.add(new JournalHeader(pageKey, moleculeEntry.getKey()));
                    if (elementObject.has("TextKey"))
                    {
                        elements.add(new JournalText(pageKey, elementObject.get("TextKey").getAsString()));
                    }
                    if (elementObject.has("Height") && elementObject.has("Width"))
                    {
                        elements.add(new JournalImage(pageKey, Compendium.Texture.GUI.moleculeImagesPrefix + pictureName, elementObject.get("Height").getAsInt(), elementObject.get("Width").getAsInt(), AlignmentMode.CENTER, FloatMode.LEFT));
                    }
                    if (elements.size()>1)
                    {
                        JournalRegistry.addPage(moleculeChapter, new EntryPage(pictureName, moleculeChapter, elements.toArray(new IJournalElement[elements.size()])));
                    }
                }
            }
            readFromObject(unparsed.entrySet(), run+1);
        }
    }
}
