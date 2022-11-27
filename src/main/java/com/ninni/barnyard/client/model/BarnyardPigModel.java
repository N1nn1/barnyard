package com.ninni.barnyard.client.model;

import com.ninni.barnyard.entities.BarnyardPig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.client.model.geom.PartNames.BODY;
import static net.minecraft.client.model.geom.PartNames.LEFT_ARM;
import static net.minecraft.client.model.geom.PartNames.LEFT_EAR;
import static net.minecraft.client.model.geom.PartNames.LEFT_LEG;
import static net.minecraft.client.model.geom.PartNames.NOSE;
import static net.minecraft.client.model.geom.PartNames.RIGHT_ARM;
import static net.minecraft.client.model.geom.PartNames.RIGHT_EAR;
import static net.minecraft.client.model.geom.PartNames.RIGHT_LEG;
import static net.minecraft.client.model.geom.PartNames.TAIL;

@SuppressWarnings("FieldCanBeLocal, unused")
@Environment(EnvType.CLIENT)
public class BarnyardPigModel extends HierarchicalModel<BarnyardPig> {
    private final ModelPart root;

    private final ModelPart body;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private final ModelPart nose;
    private final ModelPart tail;
    private final ModelPart leftEar;
    private final ModelPart rightEar;

    public BarnyardPigModel(ModelPart root) {
        this.root = root;
        
        this.body = root.getChild(BODY);
        this.leftArm = root.getChild(LEFT_ARM);
        this.rightArm = root.getChild(RIGHT_ARM);
        this.leftLeg = root.getChild(LEFT_LEG);
        this.rightLeg = root.getChild(RIGHT_LEG);

        this.nose = this.body.getChild(NOSE);
        this.tail = this.body.getChild(TAIL);
        this.leftEar = this.body.getChild(LEFT_EAR);
        this.rightEar = this.body.getChild(RIGHT_EAR);
    }

    public static LayerDefinition getLayerDefinition(CubeDeformation cubeDeformation) {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild(
                BODY,
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-5.0F, -6.0F, -7.0F, 10.0F, 10.0F, 15.0F, cubeDeformation),
                PartPose.offset(0.0F, 17.0F, 0.0F)
        );

        PartDefinition leftEar = body.addOrReplaceChild(
                LEFT_EAR,
                CubeListBuilder.create()
                        .texOffs(35, 8)
                        .addBox(-3.0F, -1.5F, -4.0F, 4.0F, 3.0F, 4.0F, cubeDeformation),
                PartPose.offsetAndRotation(5.0F, -3.5F, -4.0F, -0.3927F, 0.0F, 0.0F)
        );

        PartDefinition rightEar = body.addOrReplaceChild(
                RIGHT_EAR,
                CubeListBuilder.create()
                        .texOffs(35, 8)
                        .mirror()
                        .addBox(-1.0F, -1.5F, -4.0F, 4.0F, 3.0F, 4.0F, cubeDeformation)
                        .mirror(false),
                PartPose.offsetAndRotation(-5.0F, -3.5F, -4.0F, -0.3927F, 0.0F, 0.0F)
        );

        PartDefinition nose = body.addOrReplaceChild(
                NOSE,
                CubeListBuilder.create()
                        .texOffs(0, 25)
                        .addBox(-2.0F, -1.5F, -2.0F, 4.0F, 3.0F, 2.0F, cubeDeformation)
                        .texOffs(0, 12)
                        .addBox(3.0F, -0.5F, -1.0F, 1.0F, 2.0F, 1.0F, cubeDeformation)
                        .texOffs(5, 13)
                        .addBox(2.0F, 0.5F, -1.0F, 1.0F, 1.0F, 1.0F, cubeDeformation)
                        .texOffs(5, 13)
                        .mirror()
                        .addBox(-3.0F, 0.5F, -1.0F, 1.0F, 1.0F, 1.0F, cubeDeformation)
                        .mirror(false)
                        .texOffs(0, 12)
                        .mirror()
                        .addBox(-4.0F, -0.5F, -1.0F, 1.0F, 2.0F, 1.0F, cubeDeformation)
                        .mirror(false),
                PartPose.offset(0.0F, 1.5F, -7.0F)
        );

        PartDefinition tail = body.addOrReplaceChild(
                TAIL,
                CubeListBuilder.create()
                        .texOffs(0, 5)
                        .addBox(0.0F, -2.5F, 0.0F, 0.0F, 3.0F, 3.0F, cubeDeformation),
                PartPose.offset(0.0F, -2.5F, 8.0F)
        );

        PartDefinition leftArm = partdefinition.addOrReplaceChild(
                LEFT_ARM,
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-1.5F, -1.5F, -1.5F, 3.0F, 5.0F, 3.0F, cubeDeformation),
                PartPose.offset(3.48F, 20.5F, -2.5F)
        );

        PartDefinition leftLeg = partdefinition.addOrReplaceChild(
                LEFT_LEG,
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-1.5F, -1.5F, -1.5F, 3.0F, 5.0F, 3.0F, cubeDeformation),
                PartPose.offset(3.0F, 20.5F, 7.5F)
        );

        PartDefinition rightLeg = partdefinition.addOrReplaceChild(
                RIGHT_LEG,
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .mirror()
                        .addBox(-1.5F, -1.5F, -1.5F, 3.0F, 5.0F, 3.0F, cubeDeformation)
                        .mirror(false),
                PartPose.offset(-3.0F, 20.5F, 7.5F)
        );

        PartDefinition rightArm = partdefinition.addOrReplaceChild(
                RIGHT_ARM,
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .mirror()
                        .addBox(-1.5F, -1.5F, -1.5F, 3.0F, 5.0F, 3.0F, cubeDeformation)
                        .mirror(false),
                PartPose.offset(-3.48F, 20.5F, -2.5F)
        );

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public void setupAnim(BarnyardPig entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        float pi = (float) Math.PI;
        float speed = 1.5f;
        float degree = 1.0f;

        this.rightLeg.xRot = Mth.cos(limbAngle * 0.7f * speed) * 1.4f * degree * limbDistance;
        this.leftLeg.xRot = Mth.cos(limbAngle * 0.7f * speed + pi) * 1.4f * degree * limbDistance;
        this.rightArm.xRot = Mth.cos(limbAngle * 0.7f * speed + pi) * 1.4f * degree * limbDistance;
        this.leftArm.xRot = Mth.cos(limbAngle * 0.7f * speed) * 1.4f * degree * limbDistance;
    }

    @Override
    public @NotNull ModelPart root() {
        return this.root;
    }

    public ModelPart getBody() {
        return this.body;
    }

}