package yeelp.stoicdummy.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Generated with BlockBench <3
 * 
 * @author Yeelp
 *
 */
@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public final class ModelStoicDummy extends ModelBase {
	private final ModelRenderer stand;
	private final ModelRenderer cover;
	private final ModelRenderer bb_main;
	private final ModelRenderer base;
	@SuppressWarnings("FieldCanBeLocal")
    private final ModelRenderer cube_r1;
	@SuppressWarnings("FieldCanBeLocal")
    private final ModelRenderer cube_r2;

	public ModelStoicDummy() {
		this.textureWidth = 128;
		this.textureHeight = 128;
		
		this.base = new ModelRenderer(this);
		this.base.setRotationPoint(0.0F, 24.0F, 0.0F);
		this.base.cubeList.add(new ModelBox(this.base, 0, 0, -7.0F, -1.0F, -7.0F, 14, 1, 14, 0.0F, false));

		this.stand = new ModelRenderer(this);
		this.stand.setRotationPoint(0.0F, 24.0F, 0.0F);
		this.stand.cubeList.add(new ModelBox(this.stand, 0, 82, 1.0F, -9.0F, -2.0F, 1, 8, 1, 0.0F, false));
		this.stand.cubeList.add(new ModelBox(this.stand, 4, 82, -2.0F, -9.0F, -2.0F, 1, 8, 1, 0.0F, false));
		this.stand.cubeList.add(new ModelBox(this.stand, 8, 82, -2.0F, -9.0F, 1.0F, 1, 8, 1, 0.0F, false));
		this.stand.cubeList.add(new ModelBox(this.stand, 12, 82, 1.0F, -9.0F, 1.0F, 1, 8, 1, 0.0F, false));

		this.cover = new ModelRenderer(this);
		this.cover.setRotationPoint(6.0F, 1.0F, 0.0F);
		this.cover.cubeList.add(new ModelBox(this.cover, 0, 49, -14.0F, -1.0F, 2.0F, 16, 14, 0, 0.0F, false));
		this.cover.cubeList.add(new ModelBox(this.cover, 0, 31, -14.0F, -1.0F, -2.0F, 16, 0, 4, 0.0F, false));
		this.cover.cubeList.add(new ModelBox(this.cover, 0, 35, -14.0F, -1.0F, -2.0F, 16, 14, 0, 0.0F, false));

		this.bb_main = new ModelRenderer(this);
		this.bb_main.setRotationPoint(0.0F, 24.0F, 0.0F);
		this.bb_main.cubeList.add(new ModelBox(this.bb_main, 0, 63, -2.0F, -24.0F, -2.0F, 4, 15, 4, 0.0F, false));
		this.bb_main.cubeList.add(new ModelBox(this.bb_main, 32, 35, 2.0F, -24.0F, -2.0F, 10, 4, 4, 0.0F, false));
		this.bb_main.cubeList.add(new ModelBox(this.bb_main, 32, 43, -12.0F, -24.0F, -2.0F, 10, 4, 4, 0.0F, false));
		this.bb_main.cubeList.add(new ModelBox(this.bb_main, 0, 15, -4.0F, -32.0F, -4.0F, 8, 8, 8, 0.0F, false));
		this.bb_main.cubeList.add(new ModelBox(this.bb_main, 42, 2, -4.0F, -32.0F, -4.0F, 8, 8, 0, 0.0F, false));

		this.cube_r1 = new ModelRenderer(this);
		this.cube_r1.setRotationPoint(2.0F, -11.0F, 0.0F);
		this.bb_main.addChild(this.cube_r1);
		setRotationAngle(this.cube_r1, 0.0F, 0.0F, -1.0036F);
		this.cube_r1.cubeList.add(new ModelBox(this.cube_r1, 32, 25, -1.0F, -6.0F, -2.0F, 12, 6, 4, 0.0F, false));

		this.cube_r2 = new ModelRenderer(this);
		this.cube_r2.setRotationPoint(-2.0F, -11.0F, 0.0F);
		this.bb_main.addChild(this.cube_r2);
		setRotationAngle(this.cube_r2, 0.0F, 0.0F, 1.0036F);
		this.cube_r2.cubeList.add(new ModelBox(this.cube_r2, 32, 15, -11.0F, -6.0F, -2.0F, 12, 6, 4, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		this.base.render(f5);
		this.stand.render(f5);
		this.cover.render(f5);
		this.bb_main.render(f5);
	}
	
	public static void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
		super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
		this.base.rotateAngleY = -(float) Math.PI/180.0f*entityIn.rotationYaw;			
	}
}
