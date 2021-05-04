clear all;
close all;
clc;

%% Define transformation Matrices

% Image to Mount Matrix
function image2mountMat = image2mountFunct(tilt, pan)

    image2mountMat = [cos(tilt)*cos(pan), cos(tilt)*sin(pain), -sin(tilt);
                      -sin(pan), cos(pan), 0;
                         sin(tilt)*cos(pan), sin(tilt)*sin(pan), cos(tilt)];
end

% Mount to Image Matrix
function mount2imageMat = mount2imageFunct(tilt, pan)
    m2iMat = image2mountFunct(tilt, pan);
    mount2imageMat = m2iMat';
end

% Body to Inertial Matrix
function body2inertialMat = body2inertialFunct(pitch, yaw, roll)

    body2inertialMat = [cos(pitch)*cos(yaw),
                        cos(pitch)*sin(yaw),
                        -sin(pitch);

                      sin(roll)*sin(pitch)*cos(yaw)-cos(roll)*sin(yaw),
                      sin(roll)*sin(pitch)*sin(yaw)+cos(roll)*cos(yaw),
                      sin(roll)*cos(pitch);

                      cos(roll)*sin(yaw)*cos(yaw)+sin(roll)*sin(yaw),
                      cos(roll)*sin(pitch)*sin(yaw)-sin(roll)*cos(yaw),
                      cos(roll)*cos(pitch)];
end

% Inertial to Body Matrix
function inertial2bodyMat = inertial2bodyFunct(pitch, yaw, roll)
    b2iMat = body2inertialFunct(pitch, yaw, roll);
    inertial2bodyMat = b2iMat'
end

b2

imageCoordFocal = ImageCoordFocalTemp * (1/norm(ImageCoordFocalTemp))

MountVector = Image2MountM * imageCoordFocal;

BodyVector = Mount2BodyM * MountVector;

NED_unitary = Body2InertialM * BodyVector;

NED_vector = NED_unitary * vector_norm;
