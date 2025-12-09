export interface ColumnProps {
  type?: "selection" | "index" | "expand";
  prop?: string;
  label?: string;
  width?: number | string;
  minWidth?: number | string;
  fixed?: "left" | "right" | boolean;
  align?: "left" | "center" | "right";
  slot?: string;
  isShow?: boolean;
  sortable?: boolean | "custom";
  showOverflowTooltip?: boolean;
  search?: SearchProps;
  enum?: EnumProps[];
}

export interface SearchProps {
  el?: string;
  props?: any;
  key?: string;
  order?: number;
  span?: number;
  offset?: number;
  defaultValue?: any;
}

export interface EnumProps {
  label: string;
  value: any;
  disabled?: boolean;
  tagType?: string;
  children?: EnumProps[];
}
