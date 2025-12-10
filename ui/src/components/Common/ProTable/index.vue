<template>
  <div class="pro-table">
    <!-- 搜索区域 -->
    <div class="search-region" v-if="searchColumns.length">
      <SearchForm :columns="searchColumns" :search-param="searchParam" :search-tool-button="searchToolButton"
        :search="search" :reset="reset" />
    </div>

    <!-- 工具栏区域 -->
    <div class="toolbar-region">
      <div class="toolbar-left">
        <slot name="toolbar"></slot>
      </div>
      <div class="toolbar-right">
        <el-tooltip :content="$t('common.refresh')" placement="top">
          <el-button circle :icon="Refresh" @click="refresh" />
        </el-tooltip>
      </div>
    </div>

    <!-- 表格区域 -->
    <div class="table-region">
      <el-table v-loading="loading" :data="tableData" border style="width: 100%" v-bind="$attrs">
        <template v-for="item in tableColumns" :key="item.prop">
          <!-- Selection Column -->
          <el-table-column v-if="item.type === 'selection'" type="selection" :width="item.width || 55"
            :align="item.align || 'center'" :fixed="item.fixed" />

          <!-- Index Column -->
          <el-table-column v-else-if="item.type === 'index'" type="index" :label="item.label || '#'"
            :width="item.width || 80" :align="item.align || 'center'" :fixed="item.fixed" />

          <!-- Custom Render / Slot Column -->
          <el-table-column v-else :prop="item.prop" :label="item.label" :width="item.width" :min-width="item.minWidth"
            :sortable="item.sortable" :show-overflow-tooltip="item.showOverflowTooltip" :fixed="item.fixed"
            :align="item.align">
            <template #default="scope">
              <!-- If slot is provided in column config -->
              <slot v-if="item.slot" :name="item.slot" :row="scope.row"></slot>

              <!-- If render function is provided (less common in Vue 3 templates but possible) -->
              <!-- Default rendering -->
              <span v-else>{{ scope.row[item.prop] }}</span>
            </template>
          </el-table-column>
        </template>
      </el-table>
    </div>

    <!-- 分页区域 -->
    <div class="pagination-region" v-if="pagination">
      <el-pagination v-model:current-page="pageable.pageNum" v-model:page-size="pageable.pageSize"
        :page-sizes="[10, 20, 50, 100]" :background="true" layout="total, sizes, prev, pager, next, jumper"
        :total="pageable.total" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed, type PropType } from "vue";
import { Refresh } from "@element-plus/icons-vue";
import SearchForm from "./SearchForm.vue";

// Props定义
const props = defineProps({
  // 列配置
  columns: {
    type: Array as PropType<any[]>,
    default: () => []
  },
  // 请求接口方法，返回Promise
  requestApi: {
    type: Function,
    required: true
  },
  // 初始搜索参数
  initParam: {
    type: Object,
    default: () => ({})
  },
  // 是否显示分页
  pagination: {
    type: Boolean,
    default: true
  },
  // 数据回调处理，用于转换后端返回的数据格式
  dataCallback: {
    type: Function,
    default: (data: any) => data
  },
  // 分页参数映射 { pageNum: 'page', pageSize: 'size' }
  pageParamKeys: {
    type: Object as PropType<{ pageNum: string; pageSize: string }>,
    default: () => ({ pageNum: 'page', pageSize: 'size' })
  },
  // 搜索栏按钮配置
  searchToolButton: {
    type: Array as PropType<any[]>,
    default: () => []
  }
});

// 状态
const loading = ref(false);
const tableData = ref([]);
const pageable = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
});

// 查询参数
const searchParam = reactive<Record<string, any>>({});

// 计算属性：分离搜索列和表格列
const searchColumns = computed(() => {
  return props.columns.filter(item => item.search);
});

const tableColumns = computed(() => {
  return props.columns.filter(item => item.isShow !== false);
});

// 获取表格数据
const getTableList = async () => {
  loading.value = true;
  try {
    // 构建请求参数
    let params: Record<string, any> = { ...props.initParam, ...searchParam };

    // 添加分页参数
    if (props.pagination) {
      params[props.pageParamKeys.pageNum] = pageable.pageNum;
      params[props.pageParamKeys.pageSize] = pageable.pageSize;
    }

    const res = await props.requestApi(params);

    // 处理返回数据
    // 兼容 records/list 和 total 字段
    if (res.code === 200) {
      const data = res.data;
      // 自动识别列表字段
      let list = data;
      if (data.records) list = data.records;
      else if (data.list) list = data.list;

      // 自动识别总数字段
      const total = data.total || 0;

      tableData.value = props.dataCallback(list);
      pageable.total = total;
    }
  } catch (error) {
    console.error(error);
  } finally {
    loading.value = false;
  }
};

// 搜索
const search = () => {
  pageable.pageNum = 1;
  getTableList();
};

// 重置
const reset = () => {
  pageable.pageNum = 1;
  // 清空搜索参数
  Object.keys(searchParam).forEach(key => {
    delete searchParam[key];
  });
  // 重新获取数据
  getTableList();
};

// 刷新
const refresh = () => {
  getTableList();
};

// 分页处理
const handleSizeChange = (val: number) => {
  pageable.pageSize = val;
  getTableList();
};

const handleCurrentChange = (val: number) => {
  pageable.pageNum = val;
  getTableList();
};

// 暴露给父组件的方法
defineExpose({
  refresh,
  search,
  reset,
  tableData
});

// 初始化
onMounted(() => {
  getTableList();
});
</script>

<style scoped>
.pro-table {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  text-align: left;
}

.search-region {
  margin: 16px 16px 0 16px;
}

.toolbar-region {
  padding: 10px 18px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.table-region {
  padding: 0 18px;
  flex: 1;
}

.pagination-region {
  padding: 15px 18px;
  display: flex;
  justify-content: flex-end;
}
</style>
