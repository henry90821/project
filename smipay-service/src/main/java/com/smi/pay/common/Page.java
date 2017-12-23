package com.smi.pay.common;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;


public class Page {
    
    public static final int PAGESIZE = 10;
    /**
	 * @property 总行数
	 */
    private int totalRowsAmount;
    /**
	 * @property 是否已计算过总行数
	 */
    private boolean rowsAmountSet; 
    /**
	 * @property  每页记录行数
	 */
    private int pageSize = PAGESIZE; 
    /**
	 * @property  当前页
	 */
    private int currentPage = 1; 
    /**
	 * @property  下一页
	 */
    private int nextPage;
    /**
	 * @property  上一页
	 */
    private int previousPage;
    /**
	 * @property  总页数
	 */
    private int totalPages;
    /**
	 * @property   是否有下一页
	 */
    private boolean hasNext;
    /**
	 * @property   是否有上一页
	 */
    private boolean hasPrevious;

    /**
	 * @property   记录开始行数
	 */
    private int pageStartRow;
    /**
	 * @property   记录结束行数
	 */
    private int pageEndRow;
    /**
	 * @property   排序字段
	 */
    private String orderBy;
    /**
	 * @property   排序方式
	 */
    private String order;



    public Page(int totalRows) {
      setTotalRowsAmount(totalRows);
    }
    
    public Page(HttpServletRequest request)
    {
        String formpagesize=request.getParameter("page.pageSize");
        formpagesize=StringUtils.isBlank(formpagesize)?"20":formpagesize;
    	this.pageSize=Integer.parseInt(formpagesize);
    	String pageno=request.getParameter("page.pageNumber");
		String totalsize=request.getParameter("page.totalSize");
		if(StringUtils.isNotBlank(pageno) && StringUtils.isNotBlank(totalsize))
		{
		  setTotalRowsAmount(Integer.parseInt(totalsize));
		  setCurrentPage(Integer.parseInt(pageno));
		}
		String orderby=request.getParameter("pager.orderBy");
		String order=request.getParameter("pager.order");
		if(StringUtils.isNotBlank(orderby)){
		this.orderBy=orderby;
		}
		if(StringUtils.isNotBlank(order)){
			this.order=order;
			}    	
    }
    
    /**
     * 根据页面传过来的信息初始化Page
     * @param request
     * @param pageSize
     */
    public Page(HttpServletRequest request,int pageSize) {
    	this.pageSize=pageSize;
    	String pageno=request.getParameter("page.pageNumber");
		String totalsize=request.getParameter("page.totalSize");
		if(StringUtils.isNotBlank(pageno) && StringUtils.isNotBlank(totalsize))
		{
		  setTotalRowsAmount(Integer.parseInt(totalsize));
		  setCurrentPage(Integer.parseInt(pageno));
		}
		String orderby=request.getParameter("pager.orderBy");
		String order=request.getParameter("pager.order");
		if(StringUtils.isNotBlank(orderby)){
		this.orderBy=orderby;
		}
		if(StringUtils.isNotBlank(order)){
			this.order=order;
			}
    }

    public Page() {}

    /**
	 * @param totalRowsAmount   The totalRowsAmount to set.
	 * @uml.property   name="totalRowsAmount"
	 */
    public void setTotalRowsAmount(int i) {
    	if(pageSize == 0)
    		pageSize = PAGESIZE;
    	
		if (!this.rowsAmountSet) {
			totalRowsAmount = i;
			totalPages = (totalRowsAmount % pageSize == 0) ? totalRowsAmount
					/ pageSize : totalRowsAmount / pageSize + 1;
			setCurrentPage(1);
			this.rowsAmountSet = true;
		}
	
	}

    /**
	 * @param currentPage   The currentPage to set.
	 * @uml.property   name="currentPage"
	 */
    public void setCurrentPage(int i) {
		currentPage = i;
		nextPage = currentPage + 1;
		previousPage = currentPage - 1;
		if (currentPage * pageSize < totalRowsAmount) {
			pageEndRow = currentPage * pageSize;
			pageStartRow = pageEndRow - pageSize + 1;
	
		} else {
			pageEndRow = totalRowsAmount;
			pageStartRow = pageSize * (totalPages - 1) + 1;
		}
		if (nextPage > totalPages) {
			hasNext = false;
		} else {
			hasNext = true;
		}
		if (previousPage == 0) {
			hasPrevious = false;
		} else {
			hasPrevious = true;
		}
	}

    /**
	 * @return   Returns the currentPage.
	 * @uml.property   name="currentPage"
	 */
    public int getCurrentPage() {
		return currentPage;
	}

    public boolean isHasNext() {
      return hasNext;
    }

    public boolean isHasPrevious() {
      return hasPrevious;
    }

    /**
	 * @return   Returns the nextPage.
	 * @uml.property   name="nextPage"
	 */
    public int getNextPage() {
		return nextPage;
	}

    /**
	 * @return   Returns the pageSize.
	 * @uml.property   name="pageSize"
	 */
    public int getPageSize() {
		return pageSize;
	}

    /**
	 * @return   Returns the previousPage.
	 * @uml.property   name="previousPage"
	 */
    public int getPreviousPage() {
		return previousPage;
	}

    /**
	 * @return   Returns the totalPages.
	 * @uml.property   name="totalPages"
	 */
    public int getTotalPages() {
		return totalPages;
	}

    /**
	 * @return   Returns the totalRowsAmount.
	 * @uml.property   name="totalRowsAmount"
	 */
    public int getTotalRowsAmount() {
		return totalRowsAmount;
	}

    /**
	 * @param hasNext   The hasNext to set.
	 * @uml.property   name="hasNext"
	 */
    public void setHasNext(boolean b) {
		hasNext = b;
	}

    /**
	 * @param hasPrevious   The hasPrevious to set.
	 * @uml.property   name="hasPrevious"
	 */
    public void setHasPrevious(boolean b) {
		hasPrevious = b;
	}

    /**
	 * @param nextPage   The nextPage to set.
	 * @uml.property   name="nextPage"
	 */
    public void setNextPage(int i) {
		nextPage = i;
	}

    /**
	 * @param pageSize   The pageSize to set.
	 * @uml.property   name="pageSize"
	 */
    public void setPageSize(int i) {
		pageSize = i;
	}

    /**
	 * @param previousPage   The previousPage to set.
	 * @uml.property   name="previousPage"
	 */
    public void setPreviousPage(int i) {
		previousPage = i;
	}

    /**
	 * @param totalPages   The totalPages to set.
	 * @uml.property   name="totalPages"
	 */
    public void setTotalPages(int i) {
		totalPages = i;
	}

    /**
	 * @return   Returns the pageEndRow.
	 * @uml.property   name="pageEndRow"
	 */
    public int getPageEndRow() {
		return pageEndRow;
	}

    /**
	 * @return   Returns the pageStartRow.
	 * @uml.property   name="pageStartRow"
	 */
    public int getPageStartRow() {
		return pageStartRow;
	}
    /**
	 * @param pageStartRow   The pageStartRow to set.
	 * @uml.property   name="pageStartRow"
	 */
    public void setPageStartRow(int pageStartRow) {
		this.pageStartRow = pageStartRow;
	}


    public String getDescription() {
      String description = "Total:" + this.getTotalRowsAmount() +
          " items " + this.getTotalPages() + " pages";
//      this.currentPage+" Previous "+this.hasPrevious +
//      " Next:"+this.hasNext+
//      " start row:"+this.pageStartRow+
//      " end row:"+this.pageEndRow;
      return description;
    }

    public String description() {
      String description = "Total:" + this.getTotalRowsAmount() +
          " items " + this.getTotalPages() + " pages,Current page:" +
          this.currentPage + " Previous " + this.hasPrevious +
          " Next:" + this.hasNext +
          " start row:" + this.pageStartRow +
          " end row:" + this.pageEndRow;
      return description;
    }


	/**
	 * @return the orderBy
	 */
	public String getOrderBy() {
		return orderBy;
	}

	/**
	 * @param orderBy the orderBy to set
	 */
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	/**
	 * @return the order
	 */
	public String getOrder() {
		return order;
	}

	/**
	 * @param order the order to set
	 */
	public void setOrder(String order) {
		this.order = order;
	}

	public static void main(String args[]) {
        Page pc = new Page();

      pc.setPageSize(4);
      pc.setTotalRowsAmount(8);
      pc.setCurrentPage(1);
      System.out.println(pc.getDescription());
//          pc.setCurrentPage(2);
//    System.out.println(pc.description());
//    pc.setCurrentPage(3);
//    System.out.println(pc.description());
    }
}
