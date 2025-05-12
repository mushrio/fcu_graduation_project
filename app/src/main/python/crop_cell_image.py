import numpy as np
import cv2
from hough_lines_detect import pairwise

def adaptive_binarize(gray_img: np.ndarray):
    blocksz = max(gray_img.shape) // 10
    if blocksz % 2 == 0:
        blocksz += 1

    binarized_img = cv2.adaptiveThreshold(gray_img, 255, cv2.ADAPTIVE_THRESH_GAUSSIAN_C, cv2.THRESH_BINARY, blocksz, 10)

    return binarized_img

def get_cell_images(img, row_borders, col_borders, cell_extend):
    img = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    img = adaptive_binarize(img)
    if cell_extend == 'auto':
        cell_extend = max(img.shape[:2]) // 400
    row_borders = sorted(list(row_borders))
    col_borders = sorted(list(col_borders))
    cropped_imgs = []
    for top, bottom in pairwise(row_borders):
        for left, right in pairwise(col_borders):
            x1 = max(left     - cell_extend, 0)
            x2 = min(right    + cell_extend, img.shape[1])
            y1 = max(top      - cell_extend, 0)
            y2 = min(bottom   + cell_extend, img.shape[0])
            cropped_imgs.append(img[y1:y2, x1:x2])
    cropped_imgs = [remove_table_line(img, 0.8, 2) for img in cropped_imgs]
    return cropped_imgs

def remove_table_line(binary_img: np.ndarray, threshold: float = 1.0, erase_thickness: int = 2) -> np.ndarray:

    # 獲取陣列的行數和列數
    rows, cols = binary_img.shape

    # 計算中心店座標
    row_center = rows // 2
    col_center = cols // 2

    # 計算行和列的閾值長度
    row_threshold = int(rows * threshold)
    col_threshold = int(cols * threshold)

    # 定義檢查一行或一列是否有超過閾值長度的連續0的函式
    def mark_zeros_line(line, threshold_pixel):
        max_count = 0
        current_count = 0
        for val in line:
            if val == 0:
                current_count += 1
            else:
                max_count = max(max_count, current_count)
                current_count = 0
        max_count = max(max_count, current_count)
        return max_count >= threshold_pixel

    # 複製原始陣列
    result = binary_img.copy()

    # 從中間分別往四方檢查
    # 上
    for i in range(row_center, -1, -1):
        if mark_zeros_line(binary_img[i, :], col_threshold):
            result[:erase_thickness + i + 1] = 255
            break
    # 下
    for i in range(row_center, rows):
        if mark_zeros_line(binary_img[i, :], col_threshold):
            result[i-erase_thickness:] = 255
            break
    # 左
    for i in range(col_center, -1, -1):
        if mark_zeros_line(binary_img[:, i], row_threshold):
            result[:, :i+erase_thickness+1] = 255
            break
    # 右
    for i in range(col_center, cols):
        if mark_zeros_line(binary_img[:, i], row_threshold):
            result[:, i-erase_thickness:] = 255
            break

    # 返回處理後的陣列
    return result