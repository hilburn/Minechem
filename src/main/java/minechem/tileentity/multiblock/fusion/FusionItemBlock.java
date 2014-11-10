package minechem.tileentity.multiblock.fusion;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class FusionItemBlock extends ItemBlock
{

<<<<<<< HEAD
	private static final String[] names =
	{
		"FusionWall", "TungstenPlating", "FusionCore"
	};

	public FusionItemBlock(Block block)
	{
		//TODO: Find matching block
		super(block);
		setHasSubtypes(true);
		setUnlocalizedName("itemBlockFusion");
	}

	@Override
	public int getMetadata(int damageValue)
	{
		return damageValue;
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack)
	{
		return "minechem." + names[itemstack.getItemDamage()];
	}
=======
    private static final String[] names =
    {
        "Fusion Wall", "Tungsten Plating", "Fusion Core"
    };

    public FusionItemBlock(int par1)
    {
        super(par1);
        setHasSubtypes(true);
        setUnlocalizedName("minechem.itemBlockFusion");
    }

    @Override
    public int getMetadata(int damageValue)
    {
        return damageValue;
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack)
    {
        return super.getUnlocalizedName(itemstack) + names[itemstack.getItemDamage()];
    }

    @Override
    public String getItemDisplayName(ItemStack itemstack)
    {
        return names[itemstack.getItemDamage()];
    }
>>>>>>> origin/master
}
