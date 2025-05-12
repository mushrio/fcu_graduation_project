import cv2
import numpy as np

def pairwise(iterable):
    it = iter(iterable)
    try:
        prev = next(it)
    except StopIteration:
        return  # 空迭代器直接結束

    for current in it:
        yield prev, current
        prev = current


def merge_near_num(arr, threshold) :
    result = []
    near = []
    for n1, n2 in pairwise(arr):
        diff = abs(n1 - n2)

        if diff < threshold:
            if len(near) > 0:
                near.append(n2)
            else:
                near.extend([n1, n2])
        else:
            if len(near) > 0:
                avg = sum(near) / len(near)
                near.clear()
                result.append(avg)
            else:
                result.append(n1)
    if len(near) > 0:
        avg = sum(near) / len(near)
        result.append(avg)
    else:
        result.append(n2)

    return np.array(result)


def hough_lines_detect(focus_template: np.ndarray, inv = False, min_gap = 0) -> tuple: # tuple[np.ndarray, np.ndarray]
    """row_border, col_border"""
    if inv:
        focus_template = 255 - focus_template

    deg_range_r = 2  # degree range radius
    thresh = 0.75  # hough lines thresh
    deg_res = 0.5  # degree resolution

    rlines = cv2.HoughLines(focus_template, 1, np.pi * deg_res / 180, int(focus_template.shape[1]*thresh), min_theta=np.pi * ((90-deg_range_r) / 180), max_theta=np.pi * ((90+deg_range_r) / 180))

    clines1 = cv2.HoughLines(focus_template, 1, np.pi * deg_res / 180, int(focus_template.shape[0]*thresh), min_theta=np.pi * ((180-deg_range_r) / 180))
    clines2 = cv2.HoughLines(focus_template, 1, np.pi * deg_res / 180, int(focus_template.shape[0]*thresh), max_theta=np.pi * (deg_range_r/180))
    clines = np.concatenate((clines1, clines2))

    # x*cos(theta) + y*sin(theta) = rho
    row_center, col_center = [side / 2 for side in focus_template.shape]
    row_lines = np.array([(rho - row_center * np.cos(theta)) / np.sin(theta) for rho, theta in rlines.reshape(-1, 2)])
    col_lines = np.array([(rho - col_center * np.sin(theta)) / np.cos(theta) for rho, theta in clines.reshape(-1, 2)])

    row_borders: np.ndarray = row_lines.astype(np.int32)
    row_borders.sort()

    col_borders: np.ndarray = col_lines.astype(np.int32)
    col_borders.sort()


    if isinstance(min_gap, int) or isinstance(min_gap, float):
        processed_row_border = merge_near_num(row_borders, min_gap).astype(int)
        processed_col_border = merge_near_num(col_borders, min_gap).astype(int)
    elif isinstance(min_gap, (tuple, list)):
        processed_row_border = merge_near_num(row_borders, min_gap[0]).astype(int)
        processed_col_border = merge_near_num(col_borders, min_gap[1]).astype(int)
    else:
        processed_row_border = row_borders
        processed_col_border = col_borders

    return processed_row_border, processed_col_border

def draw_table_line(template, rows, cols):
    table_top = max(rows)
    table_bottom = min(rows)
    table_left = min(cols)
    table_right = max(cols)
    for row in rows:
        cv2.line(template, (row, table_left), (row, table_right), (255, 0, 0), 5)
    for col in cols:
        cv2.line(template, (table_top, col), (table_bottom, col), (255, 0, 0), 5)