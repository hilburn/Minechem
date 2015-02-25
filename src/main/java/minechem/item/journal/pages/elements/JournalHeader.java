package minechem.item.journal.pages.elements;

import minechem.helper.LocalizationHelper;
import net.afterlifelochie.fontbox.document.Element;
import net.afterlifelochie.fontbox.document.Heading;
import net.minecraft.entity.player.EntityPlayer;

public class JournalHeader extends JournalElement
{
    private String titleKey;

    public JournalHeader(String pageKey)
    {
        super(pageKey);
        titleKey = "journal" + (pageKey.isEmpty() ? "" : "." + pageKey) + ".title";
    }

    public JournalHeader(String pageKey, String title)
    {
        super(pageKey);
        titleKey = title;
    }

    @Override
    public Element getElement(EntityPlayer player)
    {
        return new Heading(getKey(), LocalizationHelper.getLocalString(titleKey));
    }
}
