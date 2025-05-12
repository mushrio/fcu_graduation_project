import cv2

def adaptive_binarize(gray_img):
    blocksz = max(gray_img.shape) // 10
    if blocksz % 2 == 0:
        blocksz += 1
    binarized_img = cv2.adaptiveThreshold(gray_img, 255, cv2.ADAPTIVE_THRESH_GAUSSIAN_C, cv2.THRESH_BINARY, blocksz, 10)
    return binarized_img
