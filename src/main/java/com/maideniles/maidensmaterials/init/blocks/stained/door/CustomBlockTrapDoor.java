package com.maideniles.maidensmaterials.init.blocks.stained.door;

import javax.annotation.Nullable;

import com.maideniles.maidensmaterials.init.MaidensBlocks;
import com.maideniles.maidensmaterials.init.MaidensItems;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CustomBlockTrapDoor extends Block
{
    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public static final PropertyBool OPEN = PropertyBool.create("open");
    public static final PropertyEnum<CustomBlockTrapDoor.DoorHalf> HALF = PropertyEnum.<CustomBlockTrapDoor.DoorHalf>create("half", CustomBlockTrapDoor.DoorHalf.class);
    protected static final AxisAlignedBB EAST_OPEN_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.1875D, 1.0D, 1.0D);
    protected static final AxisAlignedBB WEST_OPEN_AABB = new AxisAlignedBB(0.8125D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB SOUTH_OPEN_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.1875D);
    protected static final AxisAlignedBB NORTH_OPEN_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.8125D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB BOTTOM_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.1875D, 1.0D);
    protected static final AxisAlignedBB TOP_AABB = new AxisAlignedBB(0.0D, 0.8125D, 0.0D, 1.0D, 1.0D, 1.0D);

    public CustomBlockTrapDoor(String name) 
	{
		super(Material.WOOD);
		
		setRegistryName(name);
		setUnlocalizedName(name);
		setHardness(2.0F);
		setResistance(20);
        
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(OPEN, Boolean.valueOf(false)).withProperty(HALF, CustomBlockTrapDoor.DoorHalf.BOTTOM));
		MaidensBlocks.BLOCKS.add(this);
		MaidensItems.ITEMS.add(new ItemBlock(this).setRegistryName(this.getRegistryName()));
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        AxisAlignedBB axisalignedbb;

        if (((Boolean)state.getValue(OPEN)).booleanValue())
        {
            switch ((EnumFacing)state.getValue(FACING))
            {
                case NORTH:
                default:
                    axisalignedbb = NORTH_OPEN_AABB;
                    break;
                case SOUTH:
                    axisalignedbb = SOUTH_OPEN_AABB;
                    break;
                case WEST:
                    axisalignedbb = WEST_OPEN_AABB;
                    break;
                case EAST:
                    axisalignedbb = EAST_OPEN_AABB;
            }
        }
        else if (state.getValue(HALF) == CustomBlockTrapDoor.DoorHalf.TOP)
        {
            axisalignedbb = TOP_AABB;
        }
        else
        {
            axisalignedbb = BOTTOM_AABB;
        }

        return axisalignedbb;
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    /**
     * Determines if an entity can path through this block
     */
    public boolean isPassable(IBlockAccess worldIn, BlockPos pos)
    {
        return !((Boolean)worldIn.getBlockState(pos).getValue(OPEN)).booleanValue();
    }

    /**
     * Called when the block is right clicked by a player.
     */
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (this.blockMaterial == Material.IRON)
        {
            return false;
        }
        else
        {
            state = state.cycleProperty(OPEN);
            worldIn.setBlockState(pos, state, 2);
            this.playSound(playerIn, worldIn, pos, ((Boolean)state.getValue(OPEN)).booleanValue());
            return true;
        }
    }

    protected void playSound(@Nullable EntityPlayer player, World worldIn, BlockPos pos, boolean p_185731_4_)
    {
        if (p_185731_4_)
        {
            int i = this.blockMaterial == Material.IRON ? 1037 : 1007;
            worldIn.playEvent(player, i, pos, 0);
        }
        else
        {
            int j = this.blockMaterial == Material.IRON ? 1036 : 1013;
            worldIn.playEvent(player, j, pos, 0);
        }
    }

    /**
     * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
     * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
     * block, etc.
     */
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (!worldIn.isRemote)
        {
            boolean flag = worldIn.isBlockPowered(pos);

            if (flag || blockIn.getDefaultState().canProvidePower())
            {
                boolean flag1 = ((Boolean)state.getValue(OPEN)).booleanValue();

                if (flag1 != flag)
                {
                    worldIn.setBlockState(pos, state.withProperty(OPEN, Boolean.valueOf(flag)), 2);
                    this.playSound((EntityPlayer)null, worldIn, pos, flag);
                }
            }
        }
    }

    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate
     */
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        IBlockState iblockstate = this.getDefaultState();

        if (facing.getAxis().isHorizontal())
        {
            iblockstate = iblockstate.withProperty(FACING, facing).withProperty(OPEN, Boolean.valueOf(false));
            iblockstate = iblockstate.withProperty(HALF, hitY > 0.5F ? CustomBlockTrapDoor.DoorHalf.TOP : CustomBlockTrapDoor.DoorHalf.BOTTOM);
        }
        else
        {
            iblockstate = iblockstate.withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(OPEN, Boolean.valueOf(false));
            iblockstate = iblockstate.withProperty(HALF, facing == EnumFacing.UP ? CustomBlockTrapDoor.DoorHalf.BOTTOM : CustomBlockTrapDoor.DoorHalf.TOP);
        }

        if (worldIn.isBlockPowered(pos))
        {
            iblockstate = iblockstate.withProperty(OPEN, Boolean.valueOf(true));
        }

        return iblockstate;
    }

    /**
     * Check whether this Block can be placed at pos, while aiming at the specified side of an adjacent block
     */
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side)
    {
        return true;
    }

    protected static EnumFacing getFacing(int meta)
    {
        switch (meta & 3)
        {
            case 0:
                return EnumFacing.NORTH;
            case 1:
                return EnumFacing.SOUTH;
            case 2:
                return EnumFacing.WEST;
            case 3:
            default:
                return EnumFacing.EAST;
        }
    }

    protected static int getMetaForFacing(EnumFacing facing)
    {
        switch (facing)
        {
            case NORTH:
                return 0;
            case SOUTH:
                return 1;
            case WEST:
                return 2;
            case EAST:
            default:
                return 3;
        }
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACING, getFacing(meta)).withProperty(OPEN, Boolean.valueOf((meta & 4) != 0)).withProperty(HALF, (meta & 8) == 0 ? CustomBlockTrapDoor.DoorHalf.BOTTOM : CustomBlockTrapDoor.DoorHalf.TOP);
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        int i = 0;
        i = i | getMetaForFacing((EnumFacing)state.getValue(FACING));

        if (((Boolean)state.getValue(OPEN)).booleanValue())
        {
            i |= 4;
        }

        if (state.getValue(HALF) == CustomBlockTrapDoor.DoorHalf.TOP)
        {
            i |= 8;
        }

        return i;
    }

    /**
     * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
    public IBlockState withRotation(IBlockState state, Rotation rot)
    {
        return state.withProperty(FACING, rot.rotate((EnumFacing)state.getValue(FACING)));
    }

    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn)
    {
        return state.withRotation(mirrorIn.toRotation((EnumFacing)state.getValue(FACING)));
    }

    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {FACING, OPEN, HALF});
    }

    public BlockFaceShape getBlockFaceShape(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_)
    {
        return (p_193383_4_ == EnumFacing.UP && p_193383_2_.getValue(HALF) == CustomBlockTrapDoor.DoorHalf.TOP || p_193383_4_ == EnumFacing.DOWN && p_193383_2_.getValue(HALF) == CustomBlockTrapDoor.DoorHalf.BOTTOM) && !((Boolean)p_193383_2_.getValue(OPEN)).booleanValue() ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity)
    {
        if (state.getValue(OPEN))
        {
            IBlockState down = world.getBlockState(pos.down());
            if (down.getBlock() == net.minecraft.init.Blocks.LADDER)
                return down.getValue(BlockLadder.FACING) == state.getValue(FACING);
        }
        return false;
    }

    public static enum DoorHalf implements IStringSerializable
    {
        TOP("top"),
        BOTTOM("bottom");

        private final String name;

        private DoorHalf(String name)
        {
            this.name = name;
        }

        public String toString()
        {
            return this.name;
        }

        public String getName()
        {
            return this.name;
        }
    }
}