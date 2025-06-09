from typing import Literal
import numpy as np
import cv2

def num_channels(img: np.ndarray):
    if len(img.shape) == 2:
        return 1
    return img.shape[-1]

def gray(img_bgr: np.ndarray) -> np.ndarray:
    return cv2.cvtColor(img_bgr, cv2.COLOR_BGR2GRAY)

def find_docs(img_containing_doc, complete_doc_shape) -> np.ndarray:

    gray_image = gray(img_containing_doc)

    edged = cv2.Canny(gray_image, 30, 100)

    edged = cv2.dilate(edged,np.ones((5,5)))
    edged = cv2.erode(edged, np.ones((5,5)))

    contours, _ = cv2.findContours(edged, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)  # [[x1, y1], ...]

    contour = max(contours, key=cv2.contourArea).reshape((-1, 2))

    torrent_rate = 0.02
    step = 0.01
    loop_count = 0
    while True:

        epsilon = torrent_rate * cv2.arcLength(contour, True)
        approx = cv2.approxPolyDP(contour,epsilon,True)
        num_angles = len(approx)
        if num_angles == 4:
            break
        if num_angles > 4: # 頂點太多，需要增加容忍值
            torrent_rate += step
        else: # 頂點太少，需要減少容忍值
            torrent_rate -= step
        step /= 2

        loop_count += 1
        if loop_count > 10: # 10次以內找不到就回傳None
            return None

    approx = approx.reshape((-1,2))

    # 排序頂點順序
    rect = np.zeros((4, 2), dtype="float32")

    s = approx.sum(axis=1)
    rect[0] = approx[np.argmin(s)] # 左上
    rect[2] = approx[np.argmax(s)] # 右下

    diff = np.diff(approx, axis=1)
    rect[1] = approx[np.argmin(diff)] # 右上
    rect[3] = approx[np.argmax(diff)] # 左下

    if complete_doc_shape == 'auto':
        width = int(((rect[1,0] - rect[0,0]) + (rect[2,0] - rect[3,0])) / 2)
        height = int(((rect[2,1] - rect[1,1]) + (rect[3,1] - rect[0,1])) / 2)
    else:
        height, width = complete_doc_shape[:2]

    dst = np.array([
        [0, 0],
        [width - 1, 0],
        [width - 1, height - 1],
        [0, height - 1]], dtype="float32")
    M = cv2.getPerspectiveTransform(rect, dst)
    warped = cv2.warpPerspective(img_containing_doc, M, (width, height))

    return warped

def scale_small(image, threshold=2500):

    if min(image.shape[:2]) > threshold:
        if image.shape[0] > image.shape[1]:
            scale = threshold / image.shape[1]
        else:
            scale = threshold / image.shape[0]
        scaled_height = int(image.shape[0] * scale)
        scaled_width = int(image.shape[1] * scale)
        image = cv2.resize(image, (scaled_height, scaled_width))

    return image
