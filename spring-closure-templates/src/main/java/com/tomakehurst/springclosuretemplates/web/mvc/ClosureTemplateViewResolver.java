package com.tomakehurst.springclosuretemplates.web.mvc;

import com.google.template.soy.SoyFileSet;
import com.google.template.soy.tofu.SoyTofu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.view.AbstractTemplateViewResolver;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClosureTemplateViewResolver extends AbstractTemplateViewResolver {

	@Autowired
	private ClosureTemplateConfig closureTemplateConfig;

	private SoyTofu compiledTemplates;

	public ClosureTemplateViewResolver() {
		super();
        setExposeSpringMacroHelpers(false);
	}

	@Override
	protected void initApplicationContext() {
		super.initApplicationContext();

		if (isCache()) {
			compiledTemplates = compileTemplates();
		}
	}

	private SoyTofu compileTemplates() {

        Resource templatesLocation = closureTemplateConfig.getTemplatesLocation();
        List<File> templateFiles;
        try {
            File baseDirectory = templatesLocation.getFile();
            if (baseDirectory.isDirectory()) {
                templateFiles = findSoyFiles(baseDirectory, closureTemplateConfig.isRecursive());
            } else {
                throw new IllegalArgumentException("Soy template base directory '" + templatesLocation + "' is not a directory");
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Soy template base directory '" + templatesLocation + "' does not exist", e);
        }

        SoyFileSet.Builder fileSetBuilder = new SoyFileSet.Builder();
        for (File templateFile: templateFiles) {
            fileSetBuilder.add(templateFile);
        }

        SoyFileSet soyFileSet = fileSetBuilder.build();
        return soyFileSet.compileToJavaObj();
	}

	public void setClosureTemplateConfig(ClosureTemplateConfig closureTemplateConfig) {
		this.closureTemplateConfig = closureTemplateConfig;
	}

	@Override
	protected Class<?> getViewClass() {
		return ClosureTemplateView.class;
	}

	@Override
	protected AbstractUrlBasedView buildView(String viewName) throws Exception {
		ClosureTemplateView view = (ClosureTemplateView) super.buildView(viewName);
		view.setTemplateName(viewName);

		if (isCache()) {
			view.setCompiledTemplates(compiledTemplates);
		} else {
			view.setCompiledTemplates(compileTemplates());
		}

		return view;
	}

	protected List<File> findSoyFiles(File baseDirectory, boolean recursive) {
        List<File> soyFiles = new ArrayList<File>();
        findSoyFiles(soyFiles, baseDirectory, recursive);
        return soyFiles;
    }

	protected void findSoyFiles(List<File> soyFiles, File baseDirectory, boolean recursive) {
        File[] files = baseDirectory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    if (file.getName().endsWith(".soy")) {
                        soyFiles.add(file);
                    }
                } else if (file.isDirectory() && recursive) {
                    findSoyFiles(soyFiles, file, recursive);
                }
            }
        } else {
            throw new IllegalArgumentException("Unable to retrieve contents of: " + baseDirectory);
        }
    }
}
