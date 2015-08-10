/*******************************************************************************
 * eGov suite of products aim to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) <2015>  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 * 	1) All versions of this program, verbatim or modified must carry this
 * 	   Legal Notice.
 *
 * 	2) Any misrepresentation of the origin of the material is prohibited. It
 * 	   is required that all modified versions of this material be marked in
 * 	   reasonable ways as different from the original version.
 *
 * 	3) This license does not grant any rights to any user of the program
 * 	   with regards to rights under trademark law for use of the trade names
 * 	   or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 ******************************************************************************/
package org.egov.ptis.actions.reports;

import static java.math.BigDecimal.ZERO;
import static org.egov.ptis.constants.PropertyTaxConstants.ADMIN_HIERARCHY_TYPE;
import static org.egov.ptis.constants.PropertyTaxConstants.NOTICE_TYPE_BILL;
import static org.egov.ptis.constants.PropertyTaxConstants.PTMODULENAME;
import static org.egov.ptis.constants.PropertyTaxConstants.ZONE;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.ResultPath;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.egov.infra.admin.master.entity.Boundary;
import org.egov.infra.admin.master.service.BoundaryService;
import org.egov.infra.filestore.entity.FileStoreMapper;
import org.egov.infra.filestore.service.FileStoreService;
import org.egov.infra.reporting.engine.ReportConstants.FileFormat;
import org.egov.infra.web.struts.actions.SearchFormAction;
import org.egov.infra.web.struts.annotation.ValidationErrorPage;
import org.egov.infra.web.utils.EgovPaginatedList;
import org.egov.infstr.ValidationError;
import org.egov.infstr.ValidationException;
import org.egov.infstr.docmgmt.DocumentManagerService;
import org.egov.infstr.docmgmt.DocumentObject;
import org.egov.infstr.search.SearchQuery;
import org.egov.infstr.search.SearchQueryHQL;
import org.egov.ptis.actions.common.CommonServices;
import org.egov.ptis.domain.dao.property.PropertyTypeMasterDAO;
import org.egov.ptis.domain.entity.property.BasicProperty;
import org.egov.ptis.domain.entity.property.Property;
import org.egov.ptis.domain.entity.property.PropertyOwnerInfo;
import org.egov.ptis.domain.entity.property.PropertyTypeMaster;
import org.egov.ptis.notice.PtNotice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;

@ParentPackage("egov")
@Namespace("/reports")
@ResultPath("/WEB-INF/jsp/")
@Results({
        @Result(name = SearchNoticesAction.SUCCESS, type = "stream", params = { "contentType", "${contentType}", "inputName",
                "fileStream", "contentDisposition", "attachment; filename=${fileName}" }),
        @Result(name = "RENDER_NOTICE", location = "/commons/htmlFileRenderer.jsp"),
        @Result(name = SearchNoticesAction.INDEX, location = "reports/searchNotices.jsp") })
public class SearchNoticesAction extends SearchFormAction {
    private static final Logger LOGGER = Logger.getLogger(SearchNoticesAction.class);
    private static final long serialVersionUID = 1L;
    protected static final String SUCCESS = "success";
    private static final String ERROR = "error";
    private static final String FROM_CLAUSE = " from PtNotice notice left join notice.basicProperty bp";
    private static final String BILL_FROM_CLAUSE = " from EgBill bill, PtNotice notice left join notice.basicProperty bp";
    private static final String ORDER_BY = " order by notice.noticeDate desc";
    private static final String BILL_ORDER_BY = " order by notice.basicProperty.address.houseNoBldgApt asc";
    private String ownerName;
    private Long zoneId;
    private Long wardId;
    private String propertyType;
    private String noticeType;
    private String noticeNumber;
    private Date noticeFromDate = null;
    private Date noticeToDate = null;
    private String indexNumber;
    private String houseNumber;
    private Map<String, String> noticeTypeMap;
    private String target = "new";
    private List<PtNotice> noticeList;
    private String contentType;
    private String fileName;
    private InputStream fileStream;
    private Long contentLength;
    DocumentManagerService<DocumentObject> documentManagerService;
    private String partNo;
    @Autowired
    private PropertyTypeMasterDAO propertyTypeMasterDAO;
    @Autowired
    @Qualifier("fileStoreService")
    protected FileStoreService fileStoreService;
    @Autowired
    private BoundaryService boundaryService;

    public SearchNoticesAction() {
        super();
    }

    @SkipValidation
    @Action(value = "/searchNotices-index")
    public String index() {
        return INDEX;
    }

    @Override
    @SuppressWarnings("unchecked")
    @ValidationErrorPage(value = INDEX)
    @Action(value = "/searchNotices-search")
    public String search() {
        LOGGER.debug("Entered into search method");

        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Owner name : " + ownerName + ", " + "Notice Type : " + noticeType + ", " + "Zone Id : "
                    + zoneId + ", " + "Ward Id : " + wardId + ", " + "Property type :" + propertyType + ", "
                    + "Notice Number : " + noticeNumber + ", " + "Notice FromDate : " + noticeFromDate + ", "
                    + "noticeToDate : " + noticeToDate + ", " + "Property Id : " + indexNumber + ", "
                    + "House Number : " + houseNumber);

        target = "searchresult";
        super.search();
        noticeList = searchResult.getList();
        if (noticeList != null && !noticeList.isEmpty()) {
            LOGGER.debug("Number of notices before owner name (if input given) filter : " + noticeList.size());
            searchOwnerNamePropType();
        }
        LOGGER.debug("Number of notices after owner name (if input given) filter : " + noticeList.size());
        LOGGER.debug("Exit from search method");

        return INDEX;
    }

    @ValidationErrorPage(value = INDEX)
    @Action(value = "/searchNotices-mergeAndDownload")
    public String mergeAndDownload() throws ValidationException {
        LOGGER.debug("Entered into mergeAndDownload method");
        final long startTime = System.currentTimeMillis();
        LOGGER.debug("mergeAndDownload : Start Time : " + startTime);
        final List<PtNotice> noticeList = getNoticeBySearchParameter();
        LOGGER.debug("Number of notices : " + (noticeList != null ? noticeList.size() : ZERO));
        if (null == noticeList || noticeList.size() <= 0) {
            addActionError(getText("notice.file.merge.unavailable"));
            return ERROR;
        }

        final List<InputStream> pdfs = new ArrayList<InputStream>();

        for (final PtNotice ptNotice : noticeList)
            try {
                if (ptNotice != null && ptNotice.getFileStore() != null) {
                    final FileStoreMapper fsm = ptNotice.getFileStore();
                    final File file = fileStoreService.fetch(fsm, PTMODULENAME);
                    final byte[] bFile = FileUtils.readFileToByteArray(file);
                    pdfs.add(new ByteArrayInputStream(bFile));
                }
            } catch (final Exception e) {
                LOGGER.error("mergeAndDownload : Getting notice failed for notice " + ptNotice, e);
                continue;
            }
        LOGGER.debug("Number of pdfs : " + (pdfs != null ? pdfs.size() : ZERO));
        try {
            final HttpServletResponse response = ServletActionContext.getResponse();
            final ByteArrayOutputStream output = new ByteArrayOutputStream();
            final byte[] data = concatPDFs(pdfs, output);
            response.setHeader("Content-disposition", "attachment;filename=" + "notice_" + noticeType + ".pdf");
            response.setContentType("application/pdf");
            response.setContentLength(data.length);
            response.getOutputStream().write(data);

        } catch (final IOException e) {
            LOGGER.error("Exception in Merge and Download : ", e);
            throw new ValidationException(Arrays.asList(new ValidationError("error", e.getMessage())));
        }
        final long endTime = System.currentTimeMillis();
        LOGGER.debug("mergeAndDownload : End Time : " + endTime);
        LOGGER.info("SearchNoticesAction | mergeAndDownload | Time taken(ms) " + (endTime - startTime));
        LOGGER.debug("Exit from mergeAndDownload method");
        return null;
    }

    @ValidationErrorPage(value = INDEX)
    @Action(value = "/searchNotices-zipAndDownload")
    public String zipAndDownload() throws ValidationException {
        LOGGER.debug("Entered into zipAndDownload method");
        final long startTime = System.currentTimeMillis();
        LOGGER.debug("zipAndDownload : Start Time : " + startTime);
        final HttpServletResponse response = ServletActionContext.getResponse();
        final List<PtNotice> noticeList = getNoticeBySearchParameter();
        LOGGER.debug("Number of notices : " + (noticeList != null ? noticeList.size() : ZERO));
        try {
            ZipOutputStream zipOutputStream;
            if (null == noticeList || noticeList.size() <= 0) {
                addActionError(getText("notice.file.zip.unavailable"));
                return ERROR;
            } else {
                zipOutputStream = new ZipOutputStream(response.getOutputStream());
                response.setHeader("Content-disposition", "attachment;filename=" + "notice_" + noticeType + ".zip");
                response.setContentType("application/zip");
            }

            for (final PtNotice ptNotice : noticeList)
                try {
                    if (ptNotice != null && ptNotice.getFileStore() != null) {
                        final FileStoreMapper fsm = ptNotice.getFileStore();
                        final File file = fileStoreService.fetch(fsm, PTMODULENAME);
                        final byte[] bFile = FileUtils.readFileToByteArray(file);
                        zipOutputStream = addFilesToZip(new ByteArrayInputStream(bFile), file.getName(),
                                zipOutputStream);
                    }
                } catch (final Exception e) {
                    LOGGER.error("zipAndDownload : Getting notice failed for notice " + ptNotice, e);
                    continue;
                }

            zipOutputStream.closeEntry();
            zipOutputStream.close();

        } catch (final IOException e) {
            LOGGER.error("Exception in Zip and Download : ", e);
            e.printStackTrace();
            throw new ValidationException(Arrays.asList(new ValidationError("error", e.getMessage())));
        }
        final long endTime = System.currentTimeMillis();
        LOGGER.debug("zipAndDownload : End Time : " + endTime);
        LOGGER.info("SearchNoticesAction | zipAndDownload | Time taken(ms) " + (endTime - startTime));
        LOGGER.debug("Exit from zipAndDownload method");
        return null;
    }

    /**
     * This method only to show Bills as Bills(file stream) saved into PT system in egpt_notice table notice_file(type blob)
     * column.
     *
     * @throws IOException
     */
    @SkipValidation
    @Action(value = "/searchNotices-showNotice")
    public String showNotice() throws IOException {
        final PtNotice ptNotice = (PtNotice) getPersistenceService().find("from PtNotice notice where noticeNo=?",
                noticeNumber);
        if (ptNotice == null) {
            addActionError(getText("DocMngr.file.unavailable"));
            return ERROR;
        }

        if (ptNotice != null && ptNotice.getFileStore() != null) {
            final FileStoreMapper fsm = ptNotice.getFileStore();
            final File file = fileStoreService.fetch(fsm, PTMODULENAME);
            final byte[] bFile = FileUtils.readFileToByteArray(file);
            final InputStream myInputStream = new ByteArrayInputStream(bFile);
            fileStream = myInputStream;
            fileName = file.getName();
            contentType = "application/pdf";
            contentLength = Long.valueOf(file.length());
        }
        return SUCCESS;
    }

    @SkipValidation
    @Action(value = "/searchNotices-reset")
    public String reset() {
        LOGGER.debug("reset : Before reset values : ownerName : " + ownerName + " zoneId : " + zoneId + " wardId : "
                + wardId + " propertyType : " + propertyType + " noticeType : " + noticeType + " noticeNumber : "
                + noticeNumber + " noticeFromDate : " + noticeFromDate + " noticeToDate : " + noticeToDate
                + " indexNumber : " + indexNumber + " houseNumber : " + houseNumber);
        ownerName = "";
        zoneId = -1l;
        wardId = -1l;
        propertyType = "-1";
        noticeType = "-1";
        noticeNumber = "";
        noticeFromDate = null;
        noticeToDate = null;
        indexNumber = "";
        houseNumber = "";
        LOGGER.debug("Exit from reset method");
        return INDEX;
    }

    @Override
    public void prepare() {
        LOGGER.debug("Entered into prepare method");
        super.prepare();
        final List<Boundary> zoneList = boundaryService.getActiveBoundariesByBndryTypeNameAndHierarchyTypeName(
                ZONE.toUpperCase(),
                ADMIN_HIERARCHY_TYPE);
        final List<PropertyTypeMaster> propTypeList = propertyTypeMasterDAO.findAll();
        addDropdownData("Zone", zoneList);
        LOGGER.debug("Zone id : " + zoneId + ", " + "Ward id : " + wardId);
        prepareWardDropDownData(zoneId != null, wardId != null);

        addDropdownData("PropTypeMaster", propTypeList);
        setNoticeTypeMap(CommonServices.getNoticeTypeMstr());

        LOGGER.debug("Zone List : " + (zoneList != null ? zoneList : ZERO));
        LOGGER.debug("Property type List : " + (propTypeList != null ? propTypeList : ZERO));
        LOGGER.debug("Notice type map size : " + (noticeTypeMap != null ? noticeTypeMap.size() : ZERO));
        LOGGER.debug("Exit from prepare method");
    }

    @SuppressWarnings("unchecked")
    private void prepareWardDropDownData(final boolean zoneExists, final boolean wardExists) {
        LOGGER.debug("Entered into prepareWardDropDownData method");
        LOGGER.debug("Zone Exists ? : " + zoneExists + ", " + "Ward Exists ? : " + wardExists);
        if (zoneExists && wardExists) {
            List<Boundary> wardNewList = new ArrayList<Boundary>();
            wardNewList = getPersistenceService()
                    .findAllBy(
                            "from Boundary BI where BI.boundaryType.name=? and BI.parent.id = ? and BI.isHistory='N' order by BI.name ",
                            "Ward", getZoneId());
            addDropdownData("wardList", wardNewList);
        } else
            addDropdownData("wardList", Collections.EMPTY_LIST);
        LOGGER.debug("Exit from prepareWardDropDownData method");
    }

    public String getBoundary(final Long boundaryId) {
        LOGGER.debug("Entered into getBoundary method");
        LOGGER.debug("Boundary Id : " + boundaryId);
        Boundary bndry = null;
        if (boundaryId != null && !boundaryId.equals(-1))
            bndry = boundaryService.getBoundaryById(boundaryId);
        LOGGER.debug("Boundary : " + bndry);
        LOGGER.debug("Exit from getBoundary method");
        return bndry.getName();
    }

    public String getPropType(final String propertyType) {
        LOGGER.debug("Entered into getPropType method");
        LOGGER.debug("Property type id : " + propertyType);
        final PropertyTypeMaster propTypeMstr = propertyTypeMasterDAO
                .findById(Integer.valueOf(propertyType), false);
        LOGGER.debug("Property type : " + propTypeMstr);
        LOGGER.debug("Exit from getPropType method");
        return propTypeMstr.getType();
    }

    /**
     * @param noticeList This method removes the notices from the list which do not match the selected Owner Name and Property
     * Type
     */
    private void searchOwnerNamePropType() {
        LOGGER.debug("Entered into searchOwnerNamePropType method");
        LOGGER.debug("searchOwnerNamePropType : Owner Name : " + ownerName + ", " + "Property Type : " + propertyType);
        LOGGER.debug("searchOwnerNamePropType : Number of notices before removal: "
                + (noticeList != null ? noticeList.size() : ZERO));
        if (ownerName != null && !ownerName.equals("") || propertyType != null && !propertyType.equals("-1")) {
            final List<PtNotice> noticeRmvList = new ArrayList<PtNotice>();
            for (final PtNotice notice : noticeList) {
                final Property prop = notice.getBasicProperty().getProperty();
                LOGGER.debug("Property : " + prop);
                if (ownerName != null && !ownerName.equals("")) {
                    boolean isOwnerExist = true;
                    // TODO PHOENIX If all owner other than current owner is
                    // required then iterate over Mutation
                    for (final PropertyOwnerInfo propOwner : notice.getBasicProperty().getPropertyOwnerInfo())
                        if (!getOwnerName().equalsIgnoreCase(propOwner.getOwner().getName())) {
                            noticeRmvList.add(notice);
                            isOwnerExist = false;
                            break;
                        }
                    if (!isOwnerExist)
                        continue;
                }
                if (propertyType != null && !propertyType.equals("-1"))
                    if (!getPropType(getPropertyType()).equals(
                            prop.getPropertyDetail().getPropertyTypeMaster().getType()))
                        noticeRmvList.add(notice);
            }
            LOGGER.debug("searchOwnerNamePropType : Number of notices to be removed : "
                    + (noticeRmvList != null ? noticeRmvList.size() : ZERO));
            noticeList.removeAll(noticeRmvList);
            LOGGER.debug("searchOwnerNamePropType : Number of notices after removal: "
                    + (noticeList != null ? noticeList.size() : ZERO));
            ((EgovPaginatedList) searchResult).setFullListSize(noticeList.size());
        }
        LOGGER.debug("Exit from searchOwnerNamePropType method");
    }

    public String getNonHistoryOwnerName(final BasicProperty basicProperty) {
        LOGGER.debug("Entered into getNonHistoryOwnerName method Basic Property " + basicProperty);
        final String NonHistoryOwnerName = basicProperty.getFullOwnerName();
        LOGGER.debug("getNonHistoryOwnerName : Non-History Owner Name : " + NonHistoryOwnerName);
        LOGGER.debug("Exit from getNonHistoryOwnerName method");
        return NonHistoryOwnerName;
    }

    @SuppressWarnings("unchecked")
    @Override
    public SearchQuery prepareQuery(final String sortField, final String sortDir) {
        LOGGER.debug("Entered into prepareQuery method");
        LOGGER.debug("Sort Field : " + sortField + ", " + "Sort Dir : " + sortDir);

        final Map<String, Object> map = getCriteriaString();

        LOGGER.debug("Exit from prepareQuery method");
        return new SearchQueryHQL(prepareSearchQuery(map.get("criteriaString")),
                prepareCountQuery(map.get("criteriaString")), (ArrayList<Object>) map.get("params"));
    }

    private Map<String, Object> getCriteriaString() {
        LOGGER.debug("Entered into getCriteriaString method");
        LOGGER.debug("Notice Type : " + noticeType + ", " + "Zone Id : " + zoneId + ", " + "Ward Id : " + wardId + ", "
                + "Notice Number : " + noticeNumber + ", " + "Notice FromDate : " + noticeFromDate + ", "
                + "noticeToDate : " + noticeToDate + ", " + "Property Id : " + indexNumber + ", " + "House Number : "
                + houseNumber);
        final Map<String, Object> map = new HashMap<String, Object>();
        final ArrayList<Object> params = new ArrayList<Object>();

        StringBuilder criteriaString = new StringBuilder();
        criteriaString = new StringBuilder(" where notice.noticeType = ?");
        params.add(noticeType);

        // To show only the active Demand Bill
        if (NOTICE_TYPE_BILL.equalsIgnoreCase(noticeType))
            criteriaString = criteriaString.append(" and bill.is_History = 'N' and bill.billNo = notice.noticeNo");

        if (zoneId != null && !zoneId.equals(-1l)) {
            criteriaString.append(" and bp.propertyID.zone.id = ?");
            params.add(zoneId);
        }
        if (wardId != null && !wardId.equals(-1l)) {
            criteriaString.append(" and bp.propertyID.ward.id = ?");
            params.add(wardId);
        }

        if (noticeNumber != null && !noticeNumber.equals("")) {
            criteriaString.append(" and notice.noticeNo = ?");
            params.add(noticeNumber);
        }
        if (noticeFromDate != null && !noticeFromDate.equals("DD/MM/YYYY")) {
            criteriaString.append(" and notice.noticeDate >= ?");
            params.add(noticeFromDate);
        }
        if (noticeToDate != null && !noticeToDate.equals("DD/MM/YYYY")) {
            final Calendar nextDate = Calendar.getInstance();
            nextDate.setTime(noticeToDate);
            nextDate.add(Calendar.DATE, 1);
            criteriaString.append(" and notice.noticeDate <= ?");
            params.add(nextDate.getTime());
        }
        if (indexNumber != null && !indexNumber.equals("")) {
            criteriaString.append(" and bp.upicNo = ?");
            params.add(indexNumber);
        }
        if (houseNumber != null && !houseNumber.equals("")) {
            criteriaString.append(" and bp.address.houseNoBldgApt like ?");
            params.add(houseNumber);
        }
        map.put("criteriaString", criteriaString);
        map.put("params", params);
        LOGGER.debug("Criteria String : " + criteriaString);
        LOGGER.debug("Exit from getCriteriaString method");
        return map;
    }

    @Override
    public Object getModel() {
        return null;
    }

    @Override
    public void validate() {
        LOGGER.debug("Entered into validate method");
        if (noticeType == null || noticeType.equals("-1"))
            addActionError(getText("mandatory.noticeType"));
        if (noticeFromDate != null && !noticeFromDate.equals("DD/MM/YYYY")
                && (noticeToDate == null || noticeToDate.equals("DD/MM/YYYY")))
            addActionError(getText("mandatory.noticeTodt"));
        if (noticeToDate != null && !noticeToDate.equals("DD/MM/YYYY")
                && (noticeFromDate == null || noticeFromDate.equals("DD/MM/YYYY")))
            addActionError(getText("mandatory.noticeFromdt"));
        if (noticeFromDate != null && !noticeFromDate.equals("DD/MM/YYYY") && noticeFromDate.after(new Date()))
            addActionError(getText("mandatory.noticeFromdtBeforeCurr"));
        if (noticeToDate != null && !noticeToDate.equals("DD/MM/YYYY") && noticeToDate.after(new Date()))
            addActionError(getText("mandatory.noticeTodtBeforeCurr"));
        if (noticeFromDate != null && !noticeFromDate.equals("DD/MM/YYYY") && noticeToDate != null
                && !noticeToDate.equals("DD/MM/YYYY") && noticeToDate.before(noticeFromDate))
            addActionError(getText("mandatory.noticeTodtgtoreqCurr"));
        LOGGER.debug("Exit from validate method");
    }

    @SuppressWarnings("unchecked")
    private List<PtNotice> getNoticeBySearchParameter() {
        LOGGER.debug("Entered into getNoticeBySearchParameter method");

        final Map<String, Object> map = getCriteriaString();

        final List<PtNotice> noticeList = persistenceService.findAllBy(prepareSearchQuery(map.get("criteriaString")),
                ((ArrayList<Object>) map.get("params")).toArray());

        LOGGER.debug("Number of notices : " + (noticeList != null ? noticeList.size() : ZERO));
        LOGGER.debug("Exit from getNoticeBySearchParameter method");
        return noticeList;
    }

    private String prepareSearchQuery(final Object criteria) {
        LOGGER.debug("Entered into Search Query, criteria=" + criteria);

        final StringBuilder searchQuery = new StringBuilder("select notice");
        searchQuery.append(noticeType.equals(NOTICE_TYPE_BILL) ? BILL_FROM_CLAUSE : FROM_CLAUSE);
        searchQuery.append(criteria);
        searchQuery.append(noticeType.equals(NOTICE_TYPE_BILL) ? BILL_ORDER_BY : ORDER_BY);
        LOGGER.debug("Search Query : " + searchQuery);

        return searchQuery.toString();
    }

    private String prepareCountQuery(final Object criteria) {
        LOGGER.debug("Entered into prepareCountQuery , criteria=" + criteria);

        final StringBuilder countQuery = new StringBuilder("select count(notice)");
        countQuery.append(noticeType.equals(NOTICE_TYPE_BILL) ? BILL_FROM_CLAUSE : FROM_CLAUSE);
        countQuery.append(criteria);
        LOGGER.debug("Count Query : " + countQuery);

        return countQuery.toString();
    }

    private byte[] concatPDFs(final List<InputStream> streamOfPDFFiles, final ByteArrayOutputStream outputStream) {
        LOGGER.debug("Entered into concatPDFs method");
        Document document = null;
        try {
            final List<InputStream> pdfs = streamOfPDFFiles;
            final List<PdfReader> readers = new ArrayList<PdfReader>();
            final Iterator<InputStream> iteratorPDFs = pdfs.iterator();

            // Create Readers for the pdfs.
            while (iteratorPDFs.hasNext()) {
                final InputStream pdf = iteratorPDFs.next();
                final PdfReader pdfReader = new PdfReader(pdf);
                readers.add(pdfReader);
                if (null == document)
                    document = new Document(pdfReader.getPageSize(1));
            }
            // Create a writer for the outputstream
            final PdfWriter writer = PdfWriter.getInstance(document, outputStream);

            document.open();
            final PdfContentByte cb = writer.getDirectContent(); // Holds the PDF
            // data

            PdfImportedPage page;
            int pageOfCurrentReaderPDF = 0;
            final Iterator<PdfReader> iteratorPDFReader = readers.iterator();

            // Loop through the PDF files and add to the output.
            while (iteratorPDFReader.hasNext()) {
                final PdfReader pdfReader = iteratorPDFReader.next();

                // Create a new page in the target for each source page.
                while (pageOfCurrentReaderPDF < pdfReader.getNumberOfPages()) {
                    document.newPage();
                    pageOfCurrentReaderPDF++;
                    page = writer.getImportedPage(pdfReader, pageOfCurrentReaderPDF);
                    cb.addTemplate(page, 0, 0);
                }
                pageOfCurrentReaderPDF = 0;
            }
            outputStream.flush();
            document.close();
            outputStream.close();

        } catch (final Exception e) {
            LOGGER.error("Exception in concat PDFs : ", e);
            e.printStackTrace();
        } finally {
            if (document.isOpen())
                document.close();
            try {
                if (outputStream != null)
                    outputStream.close();
            } catch (final IOException ioe) {
                LOGGER.error("Exception in concat PDFs : ", ioe);
                ioe.printStackTrace();
            }
        }
        LOGGER.debug("Exit from concatPDFs method");
        return outputStream.toByteArray();
    }

    protected String getContentDisposition(final FileFormat fileFormat) {
        return "inline; filename=report." + fileFormat.toString();
    }

    private ZipOutputStream addFilesToZip(final InputStream inputStream, final String noticeNo, final ZipOutputStream out) {
        LOGGER.debug("Entered into addFilesToZip method");
        final byte[] buffer = new byte[1024];
        try {
            out.setLevel(Deflater.DEFAULT_COMPRESSION);
            out.putNextEntry(new ZipEntry(noticeNo.replaceAll("/", "_")));
            int len;
            while ((len = inputStream.read(buffer)) > 0)
                out.write(buffer, 0, len);
            inputStream.close();

        } catch (final IllegalArgumentException iae) {
            LOGGER.error("Exception in addFilesToZip : ", iae);
            iae.printStackTrace();
            throw new ValidationException(Arrays.asList(new ValidationError("error", iae.getMessage())));
        } catch (final FileNotFoundException fnfe) {
            LOGGER.error("Exception in addFilesToZip : ", fnfe);
            fnfe.printStackTrace();
            throw new ValidationException(Arrays.asList(new ValidationError("error", fnfe.getMessage())));
        } catch (final IOException ioe) {
            LOGGER.error("Exception in addFilesToZip : ", ioe);
            ioe.printStackTrace();
            throw new ValidationException(Arrays.asList(new ValidationError("error", ioe.getMessage())));
        }
        LOGGER.debug("Exit from addFilesToZip method");
        return out;
    }

    public String getFormattedBndryStr(final Boundary boundary) {
        LOGGER.debug("Entered into getFormattedBndryStr method");
        LOGGER.debug("boundary : " + boundary);
        final StringBuilder formattedStr = new StringBuilder();
        if (boundary != null)
            formattedStr.append(boundary.getBoundaryNum().toString()).append("-").append(boundary.getName());
        LOGGER.debug("formattedStr : " + formattedStr.toString());
        LOGGER.debug("Exit from getFormattedBndryStr method");
        return formattedStr.toString();
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(final String ownerName) {
        this.ownerName = ownerName;
    }

    public Long getZoneId() {
        return zoneId;
    }

    public void setZoneId(final Long zoneId) {
        this.zoneId = zoneId;
    }

    public Long getWardId() {
        return wardId;
    }

    public void setWardId(final Long wardId) {
        this.wardId = wardId;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(final String propertyType) {
        this.propertyType = propertyType;
    }

    public String getNoticeType() {
        return noticeType;
    }

    public void setNoticeType(final String noticeType) {
        this.noticeType = noticeType;
    }

    public String getNoticeNumber() {
        return noticeNumber;
    }

    public void setNoticeNumber(final String noticeNumber) {
        this.noticeNumber = noticeNumber;
    }

    public Date getNoticeFromDate() {
        return noticeFromDate;
    }

    public void setNoticeFromDate(final Date noticeFromDate) {
        this.noticeFromDate = noticeFromDate;
    }

    public Date getNoticeToDate() {
        return noticeToDate;
    }

    public void setNoticeToDate(final Date noticeToDate) {
        this.noticeToDate = noticeToDate;
    }

    public String getIndexNumber() {
        return indexNumber;
    }

    public void setIndexNumber(final String indexNumber) {
        this.indexNumber = indexNumber;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(final String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public Map<String, String> getNoticeTypeMap() {
        return noticeTypeMap;
    }

    public void setNoticeTypeMap(final Map<String, String> noticeTypeMap) {
        this.noticeTypeMap = noticeTypeMap;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(final String target) {
        this.target = target;
    }

    public List<PtNotice> getNoticeList() {
        return noticeList;
    }

    public void setNoticeList(final List<PtNotice> noticeList) {
        this.noticeList = noticeList;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(final String contentType) {
        this.contentType = contentType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    public InputStream getFileStream() {
        return fileStream;
    }

    public void setFileStream(final InputStream fileStream) {
        this.fileStream = fileStream;
    }

    public Long getContentLength() {
        return contentLength;
    }

    public void setContentLength(final Long contentLength) {
        this.contentLength = contentLength;
    }

    public void setDocumentManagerService(final DocumentManagerService<DocumentObject> documentManagerService) {
        this.documentManagerService = documentManagerService;
    }

    public String getPartNo() {
        return partNo;
    }

    public void setPartNo(final String partNo) {
        this.partNo = partNo;
    }
}
