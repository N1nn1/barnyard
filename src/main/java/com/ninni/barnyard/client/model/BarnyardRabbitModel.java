package com.ninni.barnyard.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ninni.barnyard.entities.BarnyardRabbit;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

@Environment(EnvType.CLIENT)
@SuppressWarnings("unused")
public class BarnyardRabbitModel extends HierarchicalModel<BarnyardRabbit> {

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart tail;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightEar;
    private final ModelPart leftEar;

    private float jumpRotation;

    public BarnyardRabbitModel(ModelPart root) {
        this.root = root;
        head = root.getChild("head");
        body = root.getChild("body");
        tail = body.getChild("tail");
        rightLeg = root.getChild("rightLeg");
        leftLeg = root.getChild("leftLeg");
        rightArm = root.getChild("rightArm");
        leftArm = root.getChild("leftArm");
        rightEar = head.getChild("rightEar");
        leftEar = head.getChild("leftEar");
    }

    public static LayerDefinition getLayerDefinition() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-2.5F, -3, -3, 5, 4, 4, new CubeDeformation(0.02F))
                        .texOffs(0, 19).addBox(-2.5F, -3, -3, 5, 4, 4, new CubeDeformation(0.25F))
                        .texOffs(0, 0).addBox(-0.5F, -0.5F, -3.5F, 1, 1, 1),
                PartPose.offset(0, 18, -2));

        PartDefinition leftEar = head.addOrReplaceChild("leftEar", CubeListBuilder.create().texOffs(24, 21).addBox(
                -0.5F, -5, -0.5F, 2, 5, 1), PartPose.offset(1, -3, -0.5F));

        PartDefinition rightEar = head.addOrReplaceChild("rightEar", CubeListBuilder.create().texOffs(18, 21)
                .addBox(-1.5F, -5, -0.5F, 2, 5, 1),
                PartPose.offset(-1, -3, -0.5F));

        PartDefinition body = partdefinition.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(0, 8).addBox(-2.5F, -1.5F, -5, 5, 4, 7,
                        new CubeDeformation(0)),
                PartPose.offsetAndRotation(0, 20.5F, 2, -0.3927F, 0, 0));

        PartDefinition tail = body
                .addOrReplaceChild("tail",
                        CubeListBuilder.create().texOffs(18, 3).addBox(-1.5F, -1.5F, 0, 3, 3, 2,
                                new CubeDeformation(0)),
                        PartPose.offsetAndRotation(0, -0.5F, 1, 0.3927F, 0, 0));

        PartDefinition rightLeg = partdefinition.addOrReplaceChild("rightLeg",
                CubeListBuilder.create().texOffs(22, 11).mirror()
                        .addBox(-1, 0, -3, 2, 1, 3).mirror(false),
                PartPose.offset(-2, 23, 3));

        PartDefinition leftLeg = partdefinition.addOrReplaceChild("leftLeg", CubeListBuilder.create().texOffs(22, 11)
                .addBox(-1, 0, -3, 2, 1, 3),
                PartPose.offset(2, 23, 3));

        PartDefinition rightArm = partdefinition.addOrReplaceChild("rightArm",
                CubeListBuilder.create().texOffs(17, 8).mirror()
                        .addBox(-0.92F, 0, -1, 2, 4, 2).mirror(false),
                PartPose.offset(-1.6F, 20, -2));

        PartDefinition leftArm = partdefinition.addOrReplaceChild("leftArm", CubeListBuilder.create().texOffs(17, 8)
                .addBox(-1.08F, 0, -1, 2, 4, 2),
                PartPose.offset(1.6F, 20, -2));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public ModelPart root() {
        return root;
    }

    @Override
    public void setupAnim(BarnyardRabbit mob, float limbAngle, float limbDistance, float age, float headYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        float k = limbAngle - mob.tickCount;
        float tilt = Math.min(limbDistance, 0.5F);

        head.xRot = headPitch * 0.017453292F;
        head.yRot = headYaw * 0.017453292F;

        rightLeg.xRot = Mth.cos(limbAngle * 0.7f) * 1.4f * limbDistance;
        leftLeg.xRot = Mth.cos(limbAngle * 0.7f) * 1.4f * limbDistance;
        rightArm.xRot = Mth.cos(limbAngle * 0.7f + Mth.PI) * 1.4f * limbDistance;
        leftArm.xRot = Mth.cos(limbAngle * 0.7f + Mth.PI) * 1.4f * limbDistance;

        leftEar.xRot = -tilt * 1.5F + Mth.cos(limbAngle * 0.9f) * 0.2f * limbDistance;
        rightEar.xRot = -tilt * 1.5F + Mth.cos(limbAngle * 0.9f) * 0.2f * limbDistance;
    }

    public void prepareMobModel(BarnyardRabbit mob, float f, float g, float h) {
        super.prepareMobModel(mob, f, g, h);
        jumpRotation = Mth.sin(mob.getJumpCompletion(h) * Mth.PI);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        rightLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        leftLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        rightArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        leftArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}