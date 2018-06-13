package com.lynn.common.result;

import java.util.Collection;

/**
 * @author liyi
 */
public class MultiResult<T> extends Result {

	private Collection<T> data;

	private long total;

	public long getTotal() {
		return total;
	}
	
	public void setTotal(long total) {
		this.total = total;
	}

	public Collection<T> getData() {
		return data;
	}

	public void setData(Collection<T> data) {
		this.data = data;
	}

	/**
	 * 请求成功调用
	 * @param data
	 * @param total
	 * @param <T>
	 * @return
	 */
	public static <T> MultiResult<T> buildSuccess(Collection<T> data,long total){
		MultiResult<T> result = new MultiResult<>();
		result.setCode(Code.SUCCESS);
		result.setData(data);
		result.setTotal(total);
		return result;
	}

	/**
	 * 请求成功调用
	 * @param data
	 * @return
	 */
	public static <T> MultiResult<T> buildSuccessWithoutTotal(Collection<T> data){
		return buildSuccess(data,0);
	}

	/**
	 * 请求失败调用
	 * @param message
	 * @return
	 */
	public static <T> MultiResult<T> buildFailure(String message){
		MultiResult<T> result = new MultiResult<>();
		result.setCode(Code.ERROR);
		result.setMessage(message);
		return result;
	}

	/**
	 * 请求失败调用
	 * 使用默认的消息字符串
	 * @return
	 */
	public static <T> MultiResult<T> buildFailure(){
		MultiResult<T> result = new MultiResult<>();
		result.setCode(Code.ERROR);
		return result;
	}
	
}
