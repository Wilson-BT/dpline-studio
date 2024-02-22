// 通用函数
import { formatDate } from '/@/utils/formatTime';

export default function () {
	//百分比格式化
	const percentFormat = (row: any, column: number, cellValue: any) => {
		return cellValue ? `${cellValue}%` : '-';
	};
	//列表日期时间格式化
	const dateFormatYMD = (row: any, column: number, cellValue: any) => {
		if (!cellValue) return '-';
		return formatDate(new Date(cellValue), 'YYYY-mm-dd');
	};
	//列表日期时间格式化
	const dateFormatYMDHMS = (row: any, column: number, cellValue: any) => {
		if (!cellValue) return '-';
		return formatDate(new Date(cellValue), 'YYYY-mm-dd HH:MM:SS');
	};
	//列表日期时间格式化
	const dateFormatHMS = (row: any, column: number, cellValue: any) => {
		if (!cellValue) return '-';
		let time = 0;
		if (typeof row === 'number') time = row;
		if (typeof cellValue === 'number') time = cellValue;
		return formatDate(new Date(time * 1000), 'HH:MM:SS');
	};
	// 小数格式化
	const scaleFormat = (value: any = 0, scale: number = 4) => {
		return Number.parseFloat(value).toFixed(scale);
	};
	// 小数格式化
	const scale2Format = (value: any = 0) => {
		return Number.parseFloat(value).toFixed(2);
	};
	return {
		percentFormat,
		dateFormatYMD,
		dateFormatYMDHMS,
		dateFormatHMS,
		scaleFormat,
		scale2Format
	};
}
