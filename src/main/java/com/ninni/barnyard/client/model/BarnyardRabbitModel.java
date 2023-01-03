package com.ninni.barnyard.client.model;

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

import static net.minecraft.client.model.geom.PartNames.*;

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

    public BarnyardRabbitModel(ModelPart root) {
        this.root = root;

        head = root.getChild(HEAD);
        body = root.getChild(BODY);
        rightLeg = root.getChild(RIGHT_LEG);
        leftLeg = root.getChild(LEFT_LEG);
        rightArm = root.getChild(RIGHT_ARM);
        leftArm = root.getChild(LEFT_ARM);

        tail = body.getChild(TAIL);

        rightEar = head.getChild(RIGHT_EAR);
        leftEar = head.getChild(LEFT_EAR);
    }

    public static LayerDefinition getLayerDefinition() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild(
                HEAD,
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-2.5F, -3, -3, 5, 4, 4, new CubeDeformation(0.02F))
                        .texOffs(0, 19)
                        .addBox(-2.5F, -3, -3, 5, 4, 4, new CubeDeformation(0.25F))
                        .texOffs(0, 0)
                        .addBox(-0.5F, -0.5F, -3.5F, 1, 1, 1),
                PartPose.offset(0, 18, -2)
        );

        PartDefinition leftEar = head.addOrReplaceChild(
                LEFT_EAR,
                CubeListBuilder.create()
                        .texOffs(24, 21)
                        .addBox(-0.5F, -5, -0.5F, 2, 5, 1),
                PartPose.offset(1, -3, -0.5F)
        );

        PartDefinition rightEar = head.addOrReplaceChild(
                RIGHT_EAR,
                CubeListBuilder.create()
                        .texOffs(18, 21)
                        .addBox(-1.5F, -5, -0.5F, 2, 5, 1),
                PartPose.offset(-1, -3, -0.5F)
        );

        PartDefinition body = partdefinition.addOrReplaceChild(
                BODY,
                CubeListBuilder.create()
                        .texOffs(0, 8)
                        .addBox(-2.5F, -1.5F, -5, 5, 4, 7, new CubeDeformation(0)),
                PartPose.offsetAndRotation(0, 20.5F, 2, -0.3927F, 0, 0)
        );

        PartDefinition tail = body.addOrReplaceChild(
                        TAIL,
                        CubeListBuilder.create()
                                .texOffs(18, 3)
                                .addBox(-1.5F, -1.5F, 0, 3, 3, 2, new CubeDeformation(0)),
                        PartPose.offsetAndRotation(0, -0.5F, 1, 0.3927F, 0, 0)
        );

        PartDefinition rightLeg = partdefinition.addOrReplaceChild(
                RIGHT_LEG,
                CubeListBuilder.create()
                        .texOffs(22, 11)
                        .mirror()
                        .addBox(-1, 0, -3, 2, 1, 3)
                        .mirror(false),
                PartPose.offset(-2, 23, 3)
        );

        PartDefinition leftLeg = partdefinition.addOrReplaceChild(
                LEFT_LEG,
                CubeListBuilder.create()
                        .texOffs(22, 11)
                        .addBox(-1, 0, -3, 2, 1, 3),
                PartPose.offset(2, 23, 3)
        );

        PartDefinition rightArm = partdefinition.addOrReplaceChild(
                RIGHT_ARM,
                CubeListBuilder.create()
                        .texOffs(17, 8)
                        .mirror()
                        .addBox(-0.92F, 0, -1, 2, 4, 2)
                        .mirror(false),
                PartPose.offset(-1.6F, 20, -2)
        );

        PartDefinition leftArm = partdefinition.addOrReplaceChild(
                LEFT_ARM,
                CubeListBuilder.create()
                        .texOffs(17, 8)
                        .addBox(-1.08F, 0, -1, 2, 4, 2),
                PartPose.offset(1.6F, 20, -2)
        );

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public ModelPart root() {
        return root;
    }

    @Override
    public void setupAnim(BarnyardRabbit mob, float limbAngle, float limbDistance, float age, float headYaw, float headPitch) {
        limbDistance = Mth.clamp(limbDistance, -0.25F, 0.25F);
        float pi = (float)Math.PI;

        float tilt = Math.min(limbDistance, 1.5F);

        head.xRot = headPitch * pi/180;
        head.yRot = headYaw * pi/180;

        leftEar.xRot = -tilt * 3;
        rightEar.xRot = -tilt * 3;

        leftEar.zRot = Mth.sin(age * 0.05F) * 0.05F;
        leftEar.xRot += Mth.cos(age * 0.025F) * 0.05F;
        rightEar.zRot = Mth.sin(age * 0.05F + pi) * 0.05F;
        rightEar.xRot += Mth.cos(age  * 0.025F + pi) * 0.05F;

        rightLeg.xRot = Mth.cos(limbAngle * 1.4f) * 2.8f * limbDistance;
        leftLeg.xRot = Mth.cos(limbAngle * 1.4f) * 2.8f * limbDistance;
        rightArm.xRot = Mth.cos(limbAngle * 1.4f + pi) * 2.8f * limbDistance;
        leftArm.xRot = Mth.cos(limbAngle * 1.4f + pi) * 2.8f * limbDistance;

        tail.yRot = Mth.cos(limbAngle * 0.7f + pi/2) * 1.4f * limbDistance;
    }
}