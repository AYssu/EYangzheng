package com.ayssu.easyverify.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页返回结果对象
 *
 * @param <T> 数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "分页返回结果")
public class PageResult<T> {

    @Schema(description = "总条数")
    private Long total;

    @Schema(description = "当前页码")
    private Integer page;

    @Schema(description = "每页大小")
    private Integer pageSize;

    @Schema(description = "总页数")
    private Integer totalPages;

    @Schema(description = "当前页数据集合")
    private List<T> items;

    /**
     * 构造方法（简化版，只传总数和数据）
     */
    public PageResult(Long total, List<T> items) {
        this.total = total;
        this.items = items;
    }

    /**
     * 构造方法（包含分页信息）
     */
    public PageResult(Long total, Integer page, Integer pageSize, List<T> items) {
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
        this.items = items;
        // 计算总页数
        this.totalPages = (int) Math.ceil((double) total / pageSize);
    }

    /**
     * 静态工厂方法
     */
    public static <T> PageResult<T> of(Long total, Integer page, Integer pageSize, List<T> items) {
        return new PageResult<>(total, page, pageSize, items);
    }
}
