package hibiii.windchimes;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class WindchimeBlock extends Block implements BlockEntityProvider {

	private static final VoxelShape SHAPE = Block.createCuboidShape(4.0, 8.0, 4.0, 12.0, 16.0, 12.0);

	public WindchimeBlock(Settings settings) {
		super(settings);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return WindchimeBlock.SHAPE;
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		BlockEntity i = world.getBlockEntity(pos);
		if(i instanceof WindchimeBlockEntity) {
			if(world.getBlockState(pos.down()).isAir()) {
				world.playSound(null, pos, Windchimes.CHIMES_SOUND, SoundCategory.RECORDS,
						0.5f + world.random.nextFloat() * 0.3f,
						0.6f + world.random.nextFloat() * 0.4f);
				((WindchimeBlockEntity)i).ring();
			}
		} else {
			i.markRemoved();
			world.setBlockState(pos, Blocks.AIR.getDefaultState());
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
		if (world.isAir(pos.up())) {
			world.getBlockEntity(pos).markRemoved();
			return Blocks.AIR.getDefaultState();
		}
		return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		return !world.isAir(pos.up()) && world.isAir(pos.down());
	}

	@Override
	public BlockEntity createBlockEntity(BlockView view) {
		return new WindchimeBlockEntity();
	}
	
	@Override
	public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
		return world.getBlockEntity(pos).onSyncedBlockEvent(type, data);
	}
}
